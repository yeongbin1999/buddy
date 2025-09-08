# 🧭 Buddy Android Engineering Standards (Jetpack Compose + Kotlin)

> 목표: 팀이 합의한 **아키텍처, 코드 스타일, 규칙**을 한 문서로 명문화하여, 읽는 즉시 같은 방식으로 개발/리뷰/테스트/배포할 수 있게 한다.

---

## 0) TL;DR 체크리스트
- **아키텍처**: Single-Activity + Navigation-Compose, MVVM, Repository, 단방향 데이터 흐름(UDF)
- **UI**: Compose + Design System(토큰/컴포넌트) + Stateless Content + Preview 필수
- **상태관리**: ViewModel(StateFlow) → UiState(불변 data class) / UiEvent(sealed) / UiEffect(shared flow)
- **네트워크**: Retrofit + OkHttp + Moshi, Repository에서만 호출, Result 래핑
- **DI**: Hilt (모듈: network, repository, datastore)
- **비동기**: Coroutine + Dispatchers(주입 가능), ViewModelScope 사용
- **로그/에러**: Timber, AppError(sealed), 전역 예외 핸들링, retry/backoff
- **테스트**: ViewModel 유닛, Repository(MockWebServer), Compose UI 테스트, CI에서 실행
- **스타일**: ktlint + detekt + Kotlin DSL Gradle, Conventional Commits, KDoc
- **리소스/문자열**: 네이밍 컨벤션, i18n, 접근성(semantics), 다크모드
- **릴리즈**: 버전 카탈로그, BuildConfig 분리(debug/release), Secrets 관리

---

## 1) 아키텍처

### 1.1 레이어
```
presentation   : Composable(Screen/Content), ViewModel(UiState/UiEvent/UiEffect)
domain (선택) : UseCase(비즈 규칙), 모델 변환
data          : Repository(조합), DataSource(API/Local), DTO, Mapper
platform      : DI(Hilt), Navigation, DesignSystem, DataStore, Logging
```

- **단방향 흐름(UDF)**: View → Event → ViewModel → State → View
- **Single Activity**: `MainActivity`는 `NavHost` 로딩만 담당, UI/로직 금지
- **Navigation-Compose**: 라우팅/백스택 제어는 NavGraph에서만

### 1.2 패키지 레이아웃
```
com.buddy.app/
 ├─ core/
 │   ├─ designsystem/{theme, components, icons}
 │   ├─ datastore/
 │   └─ util/
 ├─ navigation/
 ├─ feature/{entry, login, signup, userinfo, interest, home}/
 │    ├─ <Feature>Contract.kt
 │    ├─ <Feature>ViewModel.kt
 │    └─ <Feature>Screen.kt   (Screen + Content 분리)
 └─ data/{api, dto, repository, mapper}
```

---

## 2) UI 규칙 (Compose)

1. **Stateless Content + Stateful Screen**
    - `Screen()`은 ViewModel을 연결, `Content()`는 **순수 UI**만
2. **디자인 시스템 먼저**
    - 토큰(Color/Spacing/Typography/Shape) → 컴포넌트(Button/TextField/Chip/StateView)
3. **Preview 필수**
    - Light/Dark, minWidth/largeWidth, 로딩/빈/에러 상태 1개 이상
4. 성능
    - `remember`, `rememberSaveable`, `derivedStateOf` 사용
    - 리스트는 `key` 지정, `Lazy*` prefer
5. 접근성
    - `contentDescription`, `semantics` 제공, 터치 타겟 ≥ 48dp
6. 문자열/리소스
    - 모든 텍스트는 `strings.xml`, 수치/간격은 토큰 사용

**예시**
```kotlin
@Composable
fun LoginScreen(onHome: () -> Unit, vm: LoginViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.effects.collect { if (it is LoginEffect.NavigateHome) onHome() } }
    LoginContent(
        state = state,
        onEmailChange = { vm.onEvent(LoginEvent.EmailChanged(it)) },
        onPasswordChange = { vm.onEvent(LoginEvent.PasswordChanged(it)) },
        onSubmit = { vm.onEvent(LoginEvent.Submit) }
    )
}
```

---

## 3) 상태/로직 규칙

- **UiState**: 불변 `data class` (모든 UI 표시 정보 포함)
- **UiEvent**: `sealed interface` (사용자/시스템 입력)
- **UiEffect**: 일회성 신호(네비/토스트/스낵바) → `SharedFlow`
- **ViewModel**: 단위 책임, 상태는 `MutableStateFlow`, 외부는 `StateFlow`
- **에러 모델**: `sealed class AppError` 로 통일 → UI에서 메시지 매핑

**Result/에러 처리 예시**
```kotlin
sealed class AppError: Throwable() {
    data object Network: AppError()
    data object Unauthorized: AppError()
    data class Unknown(val cause: Throwable?): AppError()
}

inline fun <T> Result<T>.mapAppError(): Result<T> = recoverCatching {
    throw when(val e = exceptionOrNull()) {
        is HttpException -> if (e.code() == 401) AppError.Unauthorized else AppError.Network
        is IOException -> AppError.Network
        else -> AppError.Unknown(e)
    }
}
```

---

## 4) 데이터 계층 규칙

- 네트워크 호출은 **Repository 전용**
- DTO ↔ Domain 모델은 **Mapper**에서 변환 (UI가 DTO에 의존 금지)
- Retrofit + OkHttp + Moshi (KotlinJsonAdapterFactory)
- 로깅: OkHttp `HttpLoggingInterceptor` (debug만 BODY)

**Repository 예시**
```kotlin
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val mapper: AuthMapper
) {
    suspend fun login(email: String, pw: String): Result<UserSession> =
        runCatching { api.login(LoginRequestDto(email, pw)) }
            .mapCatching { mapper.toSession(it) }
            .mapAppError()
}
```

---

## 5) 비동기/Coroutine 규칙

- ViewModel: `viewModelScope`
- blocking 금지 → `withContext(Dispatchers.IO)` (또는 디스패처 주입)
- Flow 규칙: cold flow 선호, UI에선 `collectAsStateWithLifecycle()`

```kotlin
@HiltViewModel
class SampleVM @Inject constructor(
    private val repo: SampleRepo,
    @IoDispatcher private val io: CoroutineDispatcher,
) : ViewModel() {
    fun load() = viewModelScope.launch(io) { /* ... */ }
}
```

---

## 6) DI(Hilt) 규칙

- 모듈: `NetworkModule`, `RepositoryModule`, `DispatcherModule`, `DataStoreModule`
- 인터페이스 타입 주입, 구현은 data 레이어에 위치
- `@Binds` vs `@Provides` 구분 사용

```kotlin
@Module @InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides fun provideRetrofit(ok: OkHttpClient, moshi: Moshi): Retrofit = /*...*/
}
```

---

## 7) 코드 스타일 & 린팅

- **Formatter**: ktlint (기본 규칙 준수)
- **정적분석**: detekt (complexity, naming, magic number 등)
- **Gradle**: Kotlin DSL + Version Catalog(`libs.versions.toml`)
- **네이밍**:
    - 파일: `FeatureScreen.kt`, `FeatureViewModel.kt`, `FeatureContract.kt`
    - 리소스: `ic_name_24`, `img_`, `bg_`, `shape_`
    - 패키지: 소문자/단수형 (`feature.login`, `data.repository`)

**ktlint Gradle 스니펫**
```kotlin
plugins { id("org.jlleitschuh.gradle.ktlint") version "12.1.0" }
ktlint { android.set(true); ignoreFailures.set(false) }
```

---

## 8) 커밋/브랜치/PR 규칙

- **Conventional Commits**
    - `feat: 로그인 폼 검증 추가`
    - `fix: InterestChip 선택 토글 버그 수정`
    - `refactor: Repository 인터페이스 분리`
- **브랜치 전략**: trunk-based (main 보호) 또는 단순 GitFlow
    - feature/* → PR → main
- **PR 체크리스트**
    - [ ] 빌드 통과 / 테스트 통과
    - [ ] UI 변화 스크린샷/영상 첨부
    - [ ] 린트/포맷 적용
    - [ ] 접근성/다크모드 확인
    - [ ] 문자열/하드코딩 키 없음

---

## 9) 테스트 전략

- **유닛**: ViewModel (Fake Repository), Mapper
- **네트워크**: MockWebServer로 Repository 테스트
- **UI**: Compose UI 테스트 (`compose-ui-test`), semantics matcher
- **스냅샷(선택)**: Paparazzi/Shot 등
- CI에서 `./gradlew lint detekt test connectedAndroidTest`

**Compose UI 테스트 예시**
```kotlin
@get:Rule val compose = createAndroidComposeRule<MainActivity>()

@Test fun login_button_disabled_when_loading() {
    compose.onNodeWithText("로그인").assertIsNotEnabled()
}
```

---

## 10) 로깅/분석/보안

- **Timber** 사용 (debug만 plant), 민감정보 로그 금지
- Crashlytics/Analytics는 opt-in, PII 최소화
- **Secrets**: gradle secrets plugin 또는 local.properties (VC에 올리지 않음)
- 네트워크: TLS 필수, 필요 시 **Cert Pinning**(OkHttp) 고려

---

## 11) 빌드/환경/피처 플래그

- `BuildConfig` 로 `API_BASE_URL` 등 주입 (debug/release 분리)
- **Remote Config**(또는 서버 플래그)로 기능 토글
- 버저닝: `versionName`, `versionCode` (SemVer 권장)

---

## 12) 리소스/i18n/다크모드

- 문자열은 모두 `strings.xml` (ko 기본, en 준비)
- 다크모드 정상 동작, 대비비율 확인
- 아이콘은 벡터 우선, mipmap은 adaptive icon

---

## 13) 문서/KDoc

- 공개 함수/클래스 KDoc 필수 (특히 Repository, Mapper, ViewModel API)
- feature 폴더 최상단 `README.md`에 화면 흐름/상태/이벤트 요약

---

## 14) 예제: Login 플로우 요약

```
EntryScreen → LoginScreen → HomeScreen

LoginScreen (View)
  └─ emits LoginEvent.Submit
      └─ LoginViewModel (로직/상태)
          └─ AuthRepository.login() (데이터)
              └─ AuthApi.login() (Retrofit)
```

UiState/UiEvent/UiEffect, Repository, 예외 매핑은 본 문서의 코드 스니펫을 표준으로 삼는다.

---

## 15) 리뷰 관점 요약 (리뷰어 체크)

- UI는 Stateless Content로 분리되어 있는가?
- ViewModel이 네트워크/DB 직접 호출하지 않는가?
- Repository가 DTO/도메인 변환을 담당하는가?
- State는 불변 data class인가? 이벤트는 sealed인가?
- 코루틴 컨텍스트/에러 핸들링이 명확한가?
- 린트/테스트/프리뷰가 포함되어 있는가?

---

### Appendix A) 필수 라이브러리
- Compose BOM, Material3, Navigation-Compose
- Hilt, Retrofit, OkHttp, Moshi
- DataStore, Coil
- Timber, ktlint, detekt
- junit, turbine, mockk, mockwebserver, compose-ui-test

### Appendix B) 버전 카탈로그 예시 (`libs.versions.toml`)
```toml
[versions]
compose = "2025.01.00"
kotlin = "2.0.0"
agp = "8.6.0"

[libraries]
androidx-compose-bom = { module = "androidx.compose:compose-bom", version.ref = "compose" }
material3 = { module = "androidx.compose.material3:material3" }
navigation-compose = { module = "androidx.navigation:navigation-compose", version = "2.8.0" }
retrofit = { module = "com.squareup.retrofit2:retrofit", version = "2.11.0" }
moshi = { module = "com.squareup.moshi:moshi-kotlin", version = "1.15.1" }
okhttp-logging = { module = "com.squareup.okhttp3:logging-interceptor", version = "4.12.0" }
hilt = { module = "com.google.dagger:hilt-android", version = "2.51.1" }
timber = { module = "com.jakewharton.timber:timber", version = "5.0.1" }
```

---

**이 문서는 Buddy 팀의 “단일 소스 오브 트루스”입니다.**  
새로운 규칙을 추가/수정할 땐 PR로 문서부터 업데이트하세요.
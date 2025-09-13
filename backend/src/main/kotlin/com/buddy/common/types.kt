package com.buddy.common.types

/** OAuth 공급자 (SNS별 별도 계정) */
enum class AuthProvider { GOOGLE, KAKAO, NAVER }

/** 유저 온보딩/사용 상태 */
enum class UserStatus { INCOMPLETE, ACTIVE, SUSPENDED }

/** 관심사 (필요 시 항목 추가/관리 화면으로 확장) */
enum class InterestType { FITNESS, MUSIC, COOKING, STUDY, GAME, TRAVEL }

/** 모임 내 역할 */
enum class GroupRole { OWNER, MEMBER }

/** 모임 가입신청 상태 */
enum class GroupMemberStatus { APPLIED, APPROVED, REJECTED }

/** 채팅 메시지 타입 */
enum class ChatMessageType { TEXT, IMAGE, SYSTEM }

/** 알림 유형 */
enum class NotificationType { GROUP_JOIN_REQUEST, GROUP_JOIN_DECISION }

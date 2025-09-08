package com.buddy.app.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.buddy.app.core.designsystem.components.BuddyPasswordField
import com.buddy.app.core.designsystem.components.BuddyPrimaryButton
import com.buddy.app.core.designsystem.components.BuddyTextField
import com.buddy.app.core.designsystem.theme.BuddyTheme
import com.buddy.app.core.designsystem.theme.LocalSpacing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// A dummy ViewModel for preview and basic structure
class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> _uiState.update { it.copy(email = event.value) }
            is LoginEvent.PasswordChanged -> _uiState.update { it.copy(password = event.value) }
            LoginEvent.Submit -> {
                // TODO: Handle login logic
            }
        }
    }
}

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold {
        padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = LocalSpacing.current.lg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BuddyTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                labelText = "Email"
            )
            Spacer(modifier = Modifier.height(LocalSpacing.current.md))
            BuddyPasswordField(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                labelText = "Password"
            )
            Spacer(modifier = Modifier.height(LocalSpacing.current.xl))
            BuddyPrimaryButton(
                text = "Login",
                onClick = { viewModel.onEvent(LoginEvent.Submit) },
                loading = uiState.isLoading
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BuddyTheme {
        LoginScreen(rememberNavController())
    }
}
package com.buddy.app.feature.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.buddy.app.core.designsystem.components.BuddyOutlinedButton
import com.buddy.app.core.designsystem.components.BuddyPrimaryButton
import com.buddy.app.core.designsystem.components.BuddyTextButton
import com.buddy.app.core.designsystem.theme.BuddyTheme
import com.buddy.app.core.designsystem.theme.LocalSpacing
import com.buddy.app.navigation.Routes

@Composable
fun EntryScreen(navController: NavController) {
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
            // App Logo placeholder
            Text(text = "Buddy App Logo")
            Spacer(modifier = Modifier.height(LocalSpacing.current.xxl))

            BuddyPrimaryButton(text = "로그인", onClick = { navController.navigate(Routes.LOGIN) })
            Spacer(modifier = Modifier.height(LocalSpacing.current.md))
            BuddyOutlinedButton(text = "회원가입", onClick = { /* TODO: Navigate to Signup */ })

            Spacer(modifier = Modifier.height(LocalSpacing.current.lg))
            BuddyTextButton(text = "이용약관 및 개인정보처리방침", onClick = { /* TODO */ })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EntryScreenPreview() {
    BuddyTheme {
        EntryScreen(rememberNavController())
    }
}

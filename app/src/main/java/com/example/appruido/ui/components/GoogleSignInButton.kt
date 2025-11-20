package com.example.appruido.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(modifier = modifier, onClick = onClick) {
        Text("Login com o Google")
    }
}
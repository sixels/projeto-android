package com.example.appruido.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hearing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appruido.ui.components.GoogleSignInButton
import com.example.appruido.ui.theme.AppRuidoTheme
import com.google.firebase.auth.FirebaseUser

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onGoogleSignInSuccess:  (FirebaseUser) -> Unit = {},
    onGoogleSignInFailure: (Exception) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Hearing,
                    modifier = Modifier.size(60.dp),
                    contentDescription = "Logo",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Medidor de Ruído\nLogin",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        GoogleSignInButton(
            modifier = Modifier.padding(bottom = 90.dp),
            onSignInSuccess = onGoogleSignInSuccess,
            onSignInFailure = { exception ->
                Log.e("LoginScreen", "Google sign-in failed", exception)
                onGoogleSignInFailure(exception)
            }
        )
    }
}
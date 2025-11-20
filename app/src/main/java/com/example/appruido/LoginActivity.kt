package com.example.appruido

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.appruido.ui.screens.LoginScreen
import com.example.appruido.ui.theme.AppRuidoTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

const val TAG = "Ruido_LoginActivity"

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        // Instantiate a Google sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false).build()

        val credentialManager = CredentialManager.create(this)


        enableEdgeToEdge()
        setContent {
            AppRuidoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { paddingValues ->
                    LoginScreen(
                        modifier = Modifier.padding(paddingValues), onGoogleSignInClicked = {
                            // Create the Credential Manager request
                            val request =
                                GetCredentialRequest.Builder().addCredentialOption(googleIdOption)
                                    .build()

                            lifecycleScope.launch {
                                try {
                                    val credentialResponse = credentialManager.getCredential(
                                        request = request,
                                        context = this@LoginActivity
                                    )
                                    handleSignIn(credentialResponse.credential)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error getting credential", e)
                                }
                            }

                        })
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            goToMainActivity()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    auth.currentUser
                    goToMainActivity()
                } else {
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun handleSignIn(credential: Credential) {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }
}


package com.example.appruido

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.appruido.ui.screens.LoginScreen
import com.example.appruido.ui.theme.AppRuidoTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)

class LoginActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "Ruido_LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Inicia a autenticação do firebase
        firebaseAuth = Firebase.auth
        // Configurações de sign-in (solicita ID)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.d(TAG, "onCreate")


        enableEdgeToEdge()
        setContent {
            AppRuidoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { paddingValues ->
                    LoginScreen(
                        modifier = Modifier.padding(top = 0.dp, bottom = paddingValues.calculateBottomPadding(), start = paddingValues.calculateStartPadding(
                            LayoutDirection.Ltr
                        ), end = paddingValues.calculateEndPadding(
                            LayoutDirection.Ltr
                        )),
                        onGoogleSignInSuccess = { firebaseUser ->
                            {
                                Log.d(TAG, "Usuário logado: ${firebaseUser.email}")
                                val token = runBlocking { firebaseUser.getIdToken(true).await() }
                                firebaseAuthWithGoogle(token.token.toString())
                            }
                        },
                        onGoogleSignInFailure = { exception ->
                            Log.w(TAG, "Falha no login com Google", exception)
                            goToMainActivity()
                        }
                    )
                }
            }
        }
    }

    // Verifica se o usuário já está logado ao iniciar
    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            //Se o usuário estiver logado, vai para a MainActivity
            Log.d(TAG, "Usuário já logado: ${currentUser.email}")
            goToMainActivity()
        }
    }

    // Método de autenticação do Firebase com o Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sucesso no login com Firebase")
                    goToMainActivity()
                } else {
                    Log.w(TAG, "Falha no login com Firebase", task.exception)
                    Toast.makeText(this, "Falha na autenticação.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Método para ir pra MainActivity
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
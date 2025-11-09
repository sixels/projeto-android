package com.example.appruido.ui.components

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.appruido.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onSignInSuccess:  (FirebaseUser) -> Unit,
    onSignInFailure: (Exception) -> Unit
) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Your Web Client ID
            .requestEmail()
            .build())
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            onSignInSuccess(authTask.result?.user!!)
                        } else {
                            onSignInFailure(authTask.exception!!)
                        }
                    }
            } catch (e: ApiException) {
                onSignInFailure(e)
            }
        } else {
            onSignInFailure(Exception("Login cancelled"))
            Log.d("GoogleSignInButton", "Login cancelled")
        }
    }

    Button(modifier = modifier, onClick = { launcher.launch(googleSignInClient.signInIntent) }) {
        Text("Login com o Google")
    }
}
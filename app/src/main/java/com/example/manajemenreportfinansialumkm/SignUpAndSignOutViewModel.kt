package com.example.manajemenreportfinansialumkm

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manajemenreportfinansialumkm.ui.home.HomeFragment
import com.facebook.AccessToken
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class SignUpAndSignOutViewModel : ViewModel() {
    private val _userLogin = MutableLiveData<FirebaseUser?>()
    val userLogin: LiveData<FirebaseUser?> get() = _userLogin

    private val _messageError  = MutableLiveData<String?>()
    val messageError: LiveData<String?> get() = _messageError


    val auth = FirebaseAuth.getInstance()

    fun sigInWitEmail(email:String, password:String) {

        if(email != Patterns.EMAIL_ADDRESS.toString()) {
            _messageError.value = "Input Email Tidak berformat Email"
            return
        }

        if(email.isEmpty()) {
            _messageError.value = "Email tidak Boleh Kosong"
            return
        }

        if(password.isEmpty()) {
            _messageError.value =" Password tidak Boleh Kosong"
            return
        }



        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if(task.isSuccessful) {
                _userLogin.value = auth.currentUser
            } else {
                Log.d(TAG, "Cek Errornya : ${task.exception}")
                _messageError.value = "Email Atau Password Salah"
            }
        }
    }

    fun sigInWithFacebook(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                _userLogin.value = auth.currentUser
                Log.d(TAG, "User Sudah Login : ${auth.currentUser?.displayName}")
            } else {
                _messageError.value = "Authentication Failed"
            }
        }
    }

    fun sigInWithGoogle(context: Context) {
        val webClientKey:String = context.getString(R.string.web_client_id)
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientKey)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()


        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                result.let { handleSigInGoogle(it.credential) }
            } catch (e:Exception) {
                Log.e(TAG, "OnFailure Auth : ${e.message}")
            }
        }
    }

    private fun handleSigInGoogle(credential:Credential) {
        if(credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdCredential = GoogleIdTokenCredential.createFrom(credential.data)

            firebaseAuthWithGoogle(googleIdCredential.idToken)
        } else {
            Log.w(TAG, "Crendetial is not of type Google ID!")
        }
    }


    private fun firebaseAuthWithGoogle(idToken:String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                _userLogin.value = auth.currentUser
            } else {
                Log.w(TAG, "SinginWithCredetial:Failure", task.exception)
            }
        }
    }


    fun signOut(context: Context) {
        // Firebase sign out
        auth.signOut()
        val credentialManager = CredentialManager.create(context)

        // When a user signs out, clear the current user credential state from all credential providers.
        viewModelScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                _userLogin.value = null
            } catch (e: ClearCredentialException) {
                Log.e(HomeFragment.TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
    }



    companion object {
        private val TAG = "Login_view_model"
    }
}
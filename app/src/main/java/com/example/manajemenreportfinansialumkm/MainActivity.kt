package com.example.manajemenreportfinansialumkm

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.manajemenreportfinansialumkm.databinding.ActivityMainBinding
import com.example.manajemenreportfinansialumkm.ui.HomeActivity
import com.example.manajemenreportfinansialumkm.ui.register.RegisterActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.manajemenreportfinansialumkm.ui.home.HomeFragment
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private var user: FirebaseUser? = null
    private var credentialManager:CredentialManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FacebookSdk.sdkInitialize(this)

        auth = Firebase.auth


        callbackManager = CallbackManager.Factory.create()

        credentialManager = CredentialManager.create(baseContext)


        // implement user authentication facebook
        user = auth.currentUser




        val loginFacebook = binding.btnFacebooklogin
        loginFacebook.setReadPermissions("email", "public_profile")
        loginFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onCancel() {
                supportActionBar?.setHomeButtonEnabled(true)
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "On Failure response  : ${error.message}")
                Toast.makeText(baseContext, "Terjadi Kesalahan : ${error.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
                    Log.d(TAG, "Cek Data Access token : ${result.accessToken}")
            }

        })


        val toRegister = binding.registerToPage
        toRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }



        val loginGoogle = binding.btnGoogleLogin
        loginGoogle.setOnClickListener{
            launchCrenditialManager()
        }



        val btnLogIn = binding.btnLogin

        binding.textFieldPassword.setEndIconOnClickListener {
            val passwordData = binding.textInputPassword
            if(passwordData.transformationMethod == PasswordTransformationMethod.getInstance()) {
                passwordData.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.textFieldPassword.endIconDrawable = getDrawable(R.drawable.eye)
            } else {
                passwordData.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.textFieldPassword.endIconDrawable = getDrawable(R.drawable.hidden)
            }
            passwordData.setSelection( passwordData.text?.length ?: 0)
        }


        btnLogIn.setOnClickListener {
            inputDataLogin()
        }

    }

    // input data login
    private fun inputDataLogin() {
        val name = binding.textInputEmail.text.toString().trim()
        val password = binding.textInputPassword.text.toString().trim()

        val database = FirebaseDatabase.getInstance()
        val dataUSer = database.getReference("users")
        dataUSer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataUser = snapshot.child(name).child("email")
                val dataPassword = snapshot.child(name).child("password")
                if(dataUser.exists() && dataPassword.value.toString() == password) {
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        Log.d(TAG, "Cek Data User Yang Mau Dikirim : ${dataUser.value.toString()}")
                        intent.putExtra(HomeActivity.DATA_USER, name)
                        startActivity(intent)
                        finish()
                } else {
                    Toast.makeText(baseContext, "Password Atau Email Salah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Cek apakah data terdapat error: ${error.message}")
            }
        })
    }


    // facebook sigin
    private fun handleFacebookAccessToken(token:AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "signInWithCrential:success")
                    user = auth.currentUser
                    user?.let { handleLogin(it) }
                } else {
                    Log.w(TAG, "SingInWithCredemtial:Failure", task.exception)
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }



    // Gooogle singin method
    private fun launchCrenditialManager(){
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager?.getCredential(
                    context = baseContext,
                    request = request
                )
                result?.let { handleSignInGoggle(it.credential) }
            } catch (e:Exception) {
                Log.e(TAG, "OnFaulu")
            }
        }
    }

    private fun handleSignInGoggle(credential: Credential) {
        if(credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdCrendential = GoogleIdTokenCredential.createFrom(credential.data)

            firebaseAuthWithGoogle(googleIdCrendential.idToken)
        } else {
            Log.w(TAG, "Crendetial is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "signInWithCredetial:success")
                    val user = auth.currentUser
                    user?.let { handleLogin(it) }
                } else {
                    Log.w(TAG, "SinginWithCredetial:Failure", task.exception)
                }
            }

    }

    private fun handleLogin(usersData:FirebaseUser) {
        if(usersData != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Mohon Untuk Login", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }


}
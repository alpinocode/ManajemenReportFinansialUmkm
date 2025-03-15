package com.example.manajemenreportfinansialumkm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.data.loginfirebase.UsersData
import com.example.manajemenreportfinansialumkm.databinding.ActivityMainBinding
import com.example.manajemenreportfinansialumkm.ui.home.HomeActivity
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
import kotlinx.coroutines.coroutineScope

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var mCallbackManager: CallbackManager
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FacebookSdk.sdkInitialize(this)

        auth = Firebase.auth


        mCallbackManager = CallbackManager.Factory.create()

        // implement user authentication facebook
        user = auth.currentUser


        val loginFacebook = binding.btnFacebooklogin
        loginFacebook.setReadPermissions("email", "public_profile")
        loginFacebook.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult>{
            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "On Failure response  : ${error.message}")
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
                    Log.d(TAG, "Cek Data Access token : ${result.accessToken}")
            }

        })


    }

    private fun handleFacebookAccessToken(token:AccessToken) {
        Log.d(TAG, "HandleFacebookAccessToken:$token")

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
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleLogin(usersData:FirebaseUser) {
        if(usersData != null) {
            val intent = Intent(this, HomeActivity::class.java )
            startActivity(intent)
        } else {
            Toast.makeText(this, "Mohon Untuk Login", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
package com.example.manajemenreportfinansialumkm

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.databinding.ActivityMainBinding
import com.example.manajemenreportfinansialumkm.ui.HomeActivity
import com.example.manajemenreportfinansialumkm.ui.register.RegisterActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import androidx.credentials.CredentialManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private var user: FirebaseUser? = null
    private var credentialManager:CredentialManager? = null
    private val signUpViewModel:SignUpAndSignOutViewModel by viewModels()

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

        observeLogin()





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
            Log.d(TAG, "Cek Apakah Button sudah di klik")
            val email = binding.textInputEmail.text.toString().trim()
            val password = binding.textInputPassword.text.toString().trim()

            signUpViewModel.sigInWitEmail(email,password)
        }


        val loginFacebook = binding.btnFacebooklogin
        loginFacebook.setReadPermissions("email", "public_profile")
        loginFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onCancel() {
                supportActionBar?.setHomeButtonEnabled(true)
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "On Failure response  : ${error.message}")
            }

            override fun onSuccess(result: LoginResult) {
                signUpViewModel.sigInWithFacebook(result.accessToken)
            }

        })


        val toRegister = binding.registerToPage
        toRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }



        val loginGoogle = binding.btnGoogleLogin
        loginGoogle.setOnClickListener{
            signUpViewModel.sigInWithGoogle(baseContext)
        }
    }


    private fun observeLogin() {
        signUpViewModel.userLogin.observe(this) { users ->
            users?.let {
                handleLogin(it)
            }
        }

        signUpViewModel.messageError.observe(this) {
            it.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


    private fun handleLogin(usersData:FirebaseUser) {
        if(usersData != null) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
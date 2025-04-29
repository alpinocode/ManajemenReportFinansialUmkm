package com.example.manajemenreportfinansialumkm.ui.Login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.SignUpAndSignOutViewModel
import com.example.manajemenreportfinansialumkm.databinding.ActivityLoginBinding
import com.example.manajemenreportfinansialumkm.ui.home.HomeActivity
import com.example.manajemenreportfinansialumkm.ui.register.RegisterActivity
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        FacebookSdk.sdkInitialize(this)

        auth = Firebase.auth


        callbackManager = CallbackManager.Factory.create()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: SignUpAndSignOutViewModel by viewModels {
            factory
        }

        viewModel.userLogin.observe(this) {
            if (it != null) {
                handleLogin(it)
            }
        }

        viewModel.messageError.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.messageSuccess.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }


        binding?.textFieldPassword?.setEndIconOnClickListener {
            showAndHidePassword()
        }

        binding?.btnLogin?.setOnClickListener {
            val email = binding?.textInputEmail?.text.toString().trim()
            val password = binding?.textInputPassword?.text.toString().trim()

            viewModel.signInWithEmail(email, password)
        }


        val loginFacebook = binding?.btnFacebooklogin
        loginFacebook?.setReadPermissions("email", "public_profile")
        loginFacebook?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                supportActionBar?.setHomeButtonEnabled(true)
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "On Failure response  : ${error.message}")
                Toast.makeText(this@LoginActivity, "${error.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: LoginResult) {
                viewModel.signInWithFacebook(result.accessToken)
            }

        })

        binding?.btnGoogleLogin?.setOnClickListener{
            viewModel.signInWithGoogle(baseContext)
        }

        binding?.registerToPage?.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun showAndHidePassword() {
        val dataPasswordData = binding?.textInputPassword
        if(dataPasswordData?.transformationMethod == PasswordTransformationMethod.getInstance()) {
            dataPasswordData?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding?.textFieldPassword?.endIconDrawable = getDrawable(R.drawable.eye)
        } else {
            dataPasswordData?.transformationMethod = PasswordTransformationMethod.getInstance()
            binding?.textFieldPassword?.endIconDrawable = getDrawable(R.drawable.hidden)
        }
        dataPasswordData?.setSelection( dataPasswordData.text?.length ?: 0)
        return
    }

    private fun handleLogin(usersData: FirebaseUser) {
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
        private const val TAG = "LoginActivity"
    }
}
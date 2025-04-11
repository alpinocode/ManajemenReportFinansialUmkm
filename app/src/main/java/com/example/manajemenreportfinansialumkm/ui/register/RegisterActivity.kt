package com.example.manajemenreportfinansialumkm.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityRegisterBinding
import com.example.manajemenreportfinansialumkm.ui.HomeActivity
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {
    private var binding:ActivityRegisterBinding? = null
//    private val registerViewModel:RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val factory:ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel:RegisterViewModel by viewModels {
            factory
        }

        viewModel.userLogin.observe(this) {
            Log.d(TAG, "Cek Username ${it?.displayName}")
            it?.let { it1 -> handleLogin(it1) }
        }

        viewModel.messageError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        binding?.btnSubmitRegister?.setOnClickListener {
            val name = binding?.textInputFullName?.text.toString().trim()
            val email = binding?.textInputEmail?.text.toString().trim()
            val password = binding?.textInputPassword?.text.toString().trim()
            val confirmPassword = binding?.textInputPasswordConfirm?.text.toString().trim()

//            registerViewModel.signUp(name, email, password, confirmPassword)
            viewModel.register(name, email, password, confirmPassword)
        }

//        observeRegister()


        binding?.textInputLayoutPassword?.setEndIconOnClickListener {
            showAndHidePassword()
        }

        binding?.textInputLayoutPasswordConfirm?.setEndIconOnClickListener {
            showAndHidePassword()
        }
    }

//    private fun observeRegister() {
//        registerViewModel.userLogin.observe(this) {
//            it?.let { it1 -> handleLogin(it1) }
//        }
//        registerViewModel.messageError.observe(this) {
//            it.let {
//                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }



    private fun handleLogin(usersData: FirebaseUser) {
        if(usersData != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Mohon Untuk Login", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAndHidePassword() {
        val dataPasswordData = binding?.textInputPassword
        if(dataPasswordData?.transformationMethod == PasswordTransformationMethod.getInstance()) {
            dataPasswordData?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding?.textInputLayoutPassword?.endIconDrawable = getDrawable(R.drawable.eye)
        } else {
            dataPasswordData?.transformationMethod = PasswordTransformationMethod.getInstance()
            binding?.textInputLayoutPassword?.endIconDrawable = getDrawable(R.drawable.hidden)
        }
        dataPasswordData?.setSelection( dataPasswordData.text?.length ?: 0)
        return
    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    companion object  {
        private val TAG = "Register Activity"
    }
}
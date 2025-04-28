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
import com.example.manajemenreportfinansialumkm.ui.home.HomeActivity
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

        binding?.btnBackRegister?.setOnClickListener {
            finish()
        }

        binding?.btnSubmitRegister?.setOnClickListener {
            val name = binding?.textInputFullName?.text.toString().trim()
            val email = binding?.textInputEmail?.text.toString().trim()
            val password = binding?.textInputPassword?.text.toString().trim()
            val confirmPassword = binding?.textInputPasswordConfirm?.text.toString().trim()

            viewModel.register(name, email, password, confirmPassword)
        }



        binding?.textInputLayoutPassword?.setEndIconOnClickListener {
            showAndHidePassword()
        }

        binding?.textInputLayoutPasswordConfirm?.setEndIconOnClickListener {
            showAndHidePasswordConfirm()
        }

        viewModel.messageSuccess.observe(this) {
            if (it != null) {
                showMessageSuccess(it)
            }
        }

        viewModel.messageError.observe(this) {
            if (it != null) {
                showMessageError(it)
            }
        }

    }



    private fun showMessageSuccess(message:String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showMessageError(message:String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

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

    private fun showAndHidePasswordConfirm() {
        val dataPasswordConfirmData = binding?.textInputPasswordConfirm
        if(dataPasswordConfirmData?.transformationMethod == PasswordTransformationMethod.getInstance()) {
            dataPasswordConfirmData?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding?.textInputLayoutPasswordConfirm?.endIconDrawable = getDrawable(R.drawable.eye)
        } else {
            dataPasswordConfirmData?.transformationMethod = PasswordTransformationMethod.getInstance()
            binding?.textInputLayoutPasswordConfirm?.endIconDrawable = getDrawable(R.drawable.hidden)
        }
        dataPasswordConfirmData?.setSelection( dataPasswordConfirmData.text?.length ?: 0)
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
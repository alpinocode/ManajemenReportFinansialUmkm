package com.example.manajemenreportfinansialumkm.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityRegisterBinding
import com.example.manajemenreportfinansialumkm.ui.helper.HelperFirebaseDatabase
import com.example.manajemenreportfinansialumkm.ui.HomeActivity
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.util.UUID

class RegisterActivity : AppCompatActivity() {
    private var binding:ActivityRegisterBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnSubmitRegister?.setOnClickListener {
            inserDatabase()
        }

        binding?.textInputLayoutPassword?.setEndIconOnClickListener {
            val dataPasswordData = binding?.textInputPassword
            if(dataPasswordData?.transformationMethod == PasswordTransformationMethod.getInstance()) {
                dataPasswordData?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding?.textInputLayoutPassword?.endIconDrawable = getDrawable(R.drawable.eye)
            } else {
                dataPasswordData?.transformationMethod = PasswordTransformationMethod.getInstance()
                binding?.textInputLayoutPassword?.endIconDrawable = getDrawable(R.drawable.hidden)
            }
            dataPasswordData?.setSelection( dataPasswordData.text?.length ?: 0)
        }

        binding?.textInputLayoutPasswordConfirm?.setEndIconOnClickListener {
            val dataPasswordConfirmData = binding?.textInputPasswordConfirm
            if(dataPasswordConfirmData?.transformationMethod == PasswordTransformationMethod.getInstance()) {
                dataPasswordConfirmData?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding?.textInputLayoutPasswordConfirm?.endIconDrawable = getDrawable(R.drawable.eye)
            } else {
                dataPasswordConfirmData?.transformationMethod = PasswordTransformationMethod.getInstance()
                binding?.textInputLayoutPasswordConfirm?.endIconDrawable = getDrawable(R.drawable.hidden)
            }
            dataPasswordConfirmData?.setSelection( dataPasswordConfirmData.text?.length ?: 0)
        }
    }

    private fun inserDatabase() {
        val name = binding?.textInputFullName?.text.toString().trim()
        val email = binding?.textInputEmail?.text.toString().trim()
        val password = binding?.textInputPassword?.text.toString().trim()
        val confirmPassword = binding?.textInputPasswordConfirm?.text.toString().trim()

        when {
            password != confirmPassword -> Toast.makeText(this, "Confirm Password Tidak sama dengan password", Toast.LENGTH_SHORT).show()
            email.isEmpty() -> Toast.makeText(this, "Email harus unique", Toast.LENGTH_SHORT).show()
            name.isEmpty() -> Toast.makeText(this, "Name Harus Ada", Toast.LENGTH_SHORT).show()
        }


        val database = Firebase.database
        val myRefenrece = database.getReference("users")

        val dataUsers = HelperFirebaseDatabase(UUID.randomUUID(),name,email,password)

        val registerData = myRefenrece.child(name).setValue(dataUsers)

        if(registerData != null) {
            Toast.makeText(this@RegisterActivity, "Akun Anda Sudah Terdaftar", Toast.LENGTH_SHORT).show()
        }


        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(HomeActivity.DATA_USER, name)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    companion object {
        private val TAG = "Register Activity"
    }
}
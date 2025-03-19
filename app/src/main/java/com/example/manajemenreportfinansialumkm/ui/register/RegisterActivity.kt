package com.example.manajemenreportfinansialumkm.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityRegisterBinding
import com.example.manajemenreportfinansialumkm.helper.HelperFirebaseDatabase
import com.example.manajemenreportfinansialumkm.ui.HomeActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import java.util.UUID

class RegisterActivity : AppCompatActivity() {
    private var binding:ActivityRegisterBinding? = null
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()

        binding?.btnSubmitRegister?.setOnClickListener {
            firebaseAuthRegister()
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
    }

    private fun firebaseAuthRegister() {
        val name = binding?.textInputFullName?.text.toString().trim()
        val email = binding?.textInputEmail?.text.toString().trim()
        val password = binding?.textInputPassword?.text.toString().trim()
        val confirmPassword = binding?.textInputPasswordConfirm?.text.toString().trim()

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Form Tidak Boleh Kosong ", Toast.LENGTH_SHORT).show()
        }

        if(password != confirmPassword) {
            Toast.makeText(this, "Password dan confirm password Tidak Sama", Toast.LENGTH_SHORT).show()
        }



        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                val user = auth.currentUser
                val database = Firebase.database.getReference("users")

                val uuid = user?.uid.toString()
                val dataUser = HelperFirebaseDatabase(uuid,name, email)
                database.child(uuid).setValue(dataUser)

                user?.let { handleLogin(it)}

            } else {
                Toast.makeText(this, "Alamat Email Sudah Ada", Toast.LENGTH_SHORT).show()
            }
        }


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



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
    companion object  {
        private val TAG = "Register Activity"
    }
}
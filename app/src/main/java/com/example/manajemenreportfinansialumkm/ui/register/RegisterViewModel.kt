package com.example.manajemenreportfinansialumkm.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.helper.HelperFirebaseDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.database

class RegisterViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    private val _userAuth = MutableLiveData<FirebaseUser>()
    val userAuth:LiveData<FirebaseUser> get() = _userAuth


    private val _messageError = MutableLiveData<String?>()
    val messageError:LiveData<String?> get() = _messageError

    fun signUp(name:String, email:String, password:String, confirmPassword:String) {
        auth = FirebaseAuth.getInstance()

        if(email != Patterns.EMAIL_ADDRESS.toString()) {
            _messageError.value = "Input Email Tidak berformat Email"
            return
        }
        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _messageError.value = "name, email, password, confirm password tidak boleh kosong"
            return
        }
        if(password != confirmPassword) {
            _messageError.value = "Password dan Confirm password tidak sama"
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                _userAuth.value = auth.currentUser

                val  user = auth.currentUser

                val database =Firebase.database.getReference("users")

                val uuid = user?.uid.toString()

                val dataUser = HelperFirebaseDatabase(uuid, name, email)
                database.child(uuid).setValue(dataUser)
            } else {
                _messageError.value = "Alamat Email Sudah Ada"
            }
        }

    }

}
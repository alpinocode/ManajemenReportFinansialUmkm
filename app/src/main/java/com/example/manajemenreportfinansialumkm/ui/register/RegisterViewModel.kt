package com.example.manajemenreportfinansialumkm.ui.register

import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {
    val userLogin = repository.userAuth

    val messageError = repository.messageError


    fun register(name:String, email:String, password:String, confirmPassword:String)  = repository.register(name, email, password, confirmPassword)

//    private val auth = FirebaseAuth.getInstance()
//
//    fun signUp(name:String, email:String, password:String, confirmPassword:String) {
//
//        if(email != Patterns.EMAIL_ADDRESS.toString()) {
//            _messageError.value = "Input Email Tidak berformat Email"
//            return
//        }
//        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//            _messageError.value = "name, email, password, confirm password tidak boleh kosong"
//            return
//        }
//        if(password != confirmPassword) {
//            _messageError.value = "Password dan Confirm password tidak sama"
//            return
//        }
//
//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
//            if(task.isSuccessful) {
//                _userLogin.value = auth.currentUser
//
//                val  user = auth.currentUser
//
//                val database =Firebase.database.getReference("users")
//
//                val uuid = user?.uid.toString()
//
//                val dataUser = UserAuth(uuid, name, email)
//                database.child(uuid).setValue(dataUser)
//            } else {
//                _messageError.value = "Alamat Email Sudah Ada"
//            }
//        }
//
//    }

}
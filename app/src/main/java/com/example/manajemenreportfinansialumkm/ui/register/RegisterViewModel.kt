package com.example.manajemenreportfinansialumkm.ui.register

import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {
    val userLogin = repository.userAuth
    val messageSuccess = repository.messageSuccess
    val messageError = repository.messageError


    fun register(name:String, email:String, password:String, confirmPassword:String)  = repository.register(name, email, password, confirmPassword)


}
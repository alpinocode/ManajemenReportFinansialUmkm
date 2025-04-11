package com.example.manajemenreportfinansialumkm

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository
import com.facebook.AccessToken


class SignUpAndSignOutViewModel(private val repository: Repository): ViewModel() {
    val userLogin = repository.userAuth
    val messageSuccess = repository.messageSuccess
    val messageError = repository.messageError


    fun signInWithFacebook(token:AccessToken) = repository.signInWithFacebook(token)

    fun signInWithGoogle(context: Context) = repository.signInWithGoogle(context)

    fun signInWithEmail(email:String, password:String) = repository.signInWithEmail(email, password)
}
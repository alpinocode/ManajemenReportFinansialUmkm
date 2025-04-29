package com.example.manajemenreportfinansialumkm.ui.home

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(private val repository: Repository) : ViewModel() {
    val userVerification = repository.userVerfication
    val userPemasukan = repository.userPemasukan
    val messageSuccess = repository.messageSuccess
    val messageError = repository.messageError
    val isLoading = repository.isLoading

    init {
        checkedEmailVerification()
        getPemasukan()
    }


     fun checkedEmailVerification() = repository.checkedEmailVerification()
    fun sendVerificationEmail() = repository.userVerification()
    fun signOut(context: Context)  = repository.signOut(context)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPemasukan() = repository.getPemasukan()

    companion object {
        private val TAG = "HomeViewModel"
    }
}
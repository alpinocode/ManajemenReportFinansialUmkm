package com.example.manajemenreportfinansialumkm.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel(private val repository: Repository) : ViewModel() {
    val userAuth = repository.userAuth

    val auth = FirebaseAuth.getInstance()

    fun logOut(id:String) = repository.logOut(id)

    fun signOut(context: Context)  = repository.signOut(context)

    companion object {
        private val TAG = "HomeViewModel"
    }
}
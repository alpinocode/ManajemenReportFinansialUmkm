package com.example.manajemenreportfinansialumkm.ui.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.manajemenreportfinansialumkm.data.loginfirebase.UsersData
import com.example.manajemenreportfinansialumkm.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private  var usersAuth:FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usersAuth = FirebaseAuth.getInstance()

        val usersData = usersAuth?.currentUser

        if(usersData != null) {
            binding.username.text = usersData.displayName
            Glide.with(this)
                .load(usersData.photoUrl)
                .into(binding.imageUser)
        }
    }




    companion object {
        val TAG = "HomeActivity"
        val DATA_USER = "data_user"
    }
}
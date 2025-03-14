package com.example.manajemenreportfinansialumkm.ui.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.data.loginfirebase.UsersData
import com.example.manajemenreportfinansialumkm.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var dataUserData: UsersData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }



    companion object {
        val TAG = "HomeActivity"
        val DATA_USER = "data_user"
    }
}
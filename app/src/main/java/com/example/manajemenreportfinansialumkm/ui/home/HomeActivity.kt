package com.example.manajemenreportfinansialumkm.ui.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val getUser = intent.getStringExtra(DATA)
        Log.d(TAG, "cek datanya : ${getUser}")
        binding.textUser.text = getUser.toString()
    }


    companion object {
        val DATA = "data"
        val TAG = "HomeActivity"
    }
}
package com.example.manajemenreportfinansialumkm.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityHomeBinding
import com.example.manajemenreportfinansialumkm.helper.NotificationShow
import com.example.manajemenreportfinansialumkm.ui.Login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {
    private var binding: ActivityHomeBinding? = null
    private lateinit var workManager: WorkManager

    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbar)


        workManager = WorkManager.getInstance(this)
        startOnTimeTask()

        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            val navView: BottomNavigationView? = binding?.navView

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment
            val navController = navHostFragment.findNavController()

            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_pembukuan, R.id.navigation_notification
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView?.setupWithNavController(navController)
        } else {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }

    private fun startOnTimeTask() {

        val auth = FirebaseAuth.getInstance().currentUser?.displayName
        if(auth != null) {
            val data = Data.Builder().putString(NotificationShow.EXTRA_NOTIFICATION, "Notification" )
                .build()

            val constaints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            periodicWorkRequest = PeriodicWorkRequest.Builder(NotificationShow::class.java, 15, TimeUnit.MINUTES)
                .setInputData(data)
                .setConstraints(constaints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "Stock Notification",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
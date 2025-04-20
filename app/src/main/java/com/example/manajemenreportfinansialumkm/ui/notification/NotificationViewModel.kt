package com.example.manajemenreportfinansialumkm.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationViewModel(private val repository: Repository) : ViewModel() {
    val notification = repository.notification
    val isLoading = repository.isLoading


    init {
        getNotification()
    }
    private fun getNotification() = repository.getStockMenipis()


    companion object {
         private val CHANNEL_ID = "channel_01"
        private val CHANNEL_NAME = "channel_name"
        const val NOTIFICATION_ID = 1
    }
}

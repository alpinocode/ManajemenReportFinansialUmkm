package com.example.manajemenreportfinansialumkm.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.example.manajemenreportfinansialumkm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationShow(context:Context, workerParameters: WorkerParameters) :CoroutineWorker(context, workerParameters){
    private val auth = FirebaseAuth.getInstance().currentUser?.displayName.toString()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val lowStockData = fetchLowStockData()
            if (lowStockData.isNotEmpty()) {

                val item = lowStockData.first()
                val lastNotificationItem = getLastNotificationItem(applicationContext)

                if (item.nameBarang != lastNotificationItem) {
                    val title = "Halo ${item.name}, Barang ${item.nameBarang} Udah Mau Habis"
                    val desc = item.keterangan ?: "Segera restock ya!"
                    showNotification(title, desc, R.drawable.logo)

                    saveLastNotificationItem(applicationContext, item.nameBarang.toString())
                }

            }
            Result.success()
        } catch (e:Exception) {
            Result.retry()
        }
    }

    private suspend fun fetchLowStockData():List<Stock> = withContext(Dispatchers.IO) {
        val result = mutableListOf<Stock>()
        val task = kotlinx.coroutines.suspendCancellableCoroutine<List<Stock>> { cont ->
            val ref = FirebaseDatabase.getInstance().getReference(auth).child("Stock").orderByChild("stock").endAt(5.0)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        val data = item.getValue(Stock::class.java)
                        data?.let {
                            result.add(it)
                        }
                    }
                    cont.resume(result, null)
                }

                override fun onCancelled(p0: DatabaseError) {
                    cont.resume(emptyList(), null)
                }
            })
        }
        return@withContext task
    }

    private fun showNotification(title:String, description:String, imageEvent:Int) {
        val bitmap = Glide.with(applicationContext)
            .asBitmap()
            .load(imageEvent)
            .submit().get()


        val notificationMangager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(title)
            .setContentText(description)
            .setLargeIcon(bitmap)
            .setPriority(Notification.PRIORITY_MAX)
            .setPriority(Notification.VISIBILITY_PRIVATE)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationMangager.createNotificationChannel(channel)
        }
        notificationMangager.notify(NOTIFICATION_ID, notification.build() )
    }

    private fun saveLastNotificationItem(context: Context, itemName:String) {
        val sharedPreferences= context.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("last_notification_item", itemName).apply()

    }

    private fun getLastNotificationItem(context: Context):String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        Log.d("NotificationShow", "getLastNotificationItem called: ${sharedPreferences}")
        return sharedPreferences.getString("last_notification_item", null)
    }

    companion object {
        private val CHANNEL_ID = "channel_01"
        private val CHANNEL_NAME = "channel_name"
        const val NOTIFICATION_ID = 1
        const val EXTRA_NOTIFICATION = "notification"
        const val SHARED_PREFERENCES_NAME = "notification_prefs"
    }
}
package com.example.manajemenreportfinansialumkm.ui.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

class NotificationViewModel(private val repository: Repository) : ViewModel() {
    val notification = repository.notification
    val isLoading = repository.isLoading
    val userStockData = repository.userStock
    val messageSuccess = repository.messageSuccess


    init {
        getNotification()
    }
    fun getNotification() = repository.getStockMenipis()

    fun getStockById(id:String) = repository.getStockById(id)

    fun updateStock(id: String, stock:Int, modal:Int) = repository.updateStockOnly(id, stock, modal)

    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePengeluaran(id:String, modal:Int) = repository.updatePengeluaran(id, modal)

}

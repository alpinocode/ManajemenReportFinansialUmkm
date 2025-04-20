package com.example.manajemenreportfinansialumkm.ui.transaction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

class TrasanctionViewModel(private val repository: Repository) : ViewModel() {
    val userTransaction = repository.userStock
    val userSearchTransaction = repository.searchItem
    val messageSuccess = repository.messageSuccess
    val messageError = repository.messageError
    val isLoading = repository.isLoading

    init {
        loadDataTransaction()
    }
    fun loadDataTransaction() = repository.getStock()
    fun searchTransaction(query:String) = repository.searchItem(query)

    fun getById(id:String) = repository.getStockById(id)

    fun updateStock(id:String, stock:Int) = repository.updateStockAfterTransaction(id, stock)
    @RequiresApi(Build.VERSION_CODES.O)
    fun addPemasukan(id:String, codeBarang:String, harga:Int, stock:Int) = repository.addPemasukanTransaction(id, codeBarang, harga, stock)
}
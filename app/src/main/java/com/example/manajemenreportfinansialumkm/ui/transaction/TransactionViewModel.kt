package com.example.manajemenreportfinansialumkm.ui.transaction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

class TransactionViewModel(private val repository: Repository) : ViewModel() {
    val userTransaction = repository.userStock
    val userSearchTransaction = repository.searchItem
    val messageSuccess = repository.messageSuccess
    val messageError = repository.messageError
    val isLoading = repository.isLoading
    val userHistoryTransaction = repository.userTransaction


    init {
        getTransactionHistory()
        loadDataTransaction()
    }
    fun loadDataTransaction() = repository.getStock()
    fun searchTransaction(query:String) = repository.searchItem(query)

    fun getById(id:String) = repository.getStockById(id)


    @RequiresApi(Build.VERSION_CODES.O)
    fun addOrder(id: String, date:String, namaBarang:String, harga: Int, quantity:Int ) = repository.addTransactionOrder(id, date, namaBarang, harga, quantity )

    fun getTransactionHistory() = repository.getTransaction()
}
package com.example.manajemenreportfinansialumkm.ui.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductViewModel(private val repository: Repository) : ViewModel() {
    val userStock = repository.userStock
    val userSearchStock = repository.searchItem
//    val messageSuccess = repository.userStockMessageSuccess
    val messageError = repository.messageError
    val isLoading = repository.isLoading



    fun loadStockInData() = repository.getStock()
    fun deleteStock(id:String) = repository.deleteStock(id)

    fun searchStock(query:String) = repository.searchItem(query)
}
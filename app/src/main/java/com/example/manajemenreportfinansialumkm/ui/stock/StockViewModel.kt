package com.example.manajemenreportfinansialumkm.ui.stock

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

class StockViewModel(private val repository: Repository) : ViewModel(){
    val messageError = repository.messageError
    val messageSuccess = repository.messageSuccess
    val userStock = repository.userStock

    fun addStock( name:String, nameSuplier:String, nameBarang:String, codeBarang:String, kategory:String, harga:Int, stock:Int, keterangan: String,  date:String) = repository.addStock(name, nameSuplier, nameBarang, codeBarang, kategory , harga,stock,keterangan,date )
    
    fun getStockById(id:String) = repository.getStockById(id)

    fun updateStock(id:String,nameSuplier:String, nameBarang:String, kategory:String, stock:Int, harga:Int,keterangan: String, date:String) = repository.updateStockData(id, nameSuplier, nameBarang, kategory, stock, harga, keterangan, date)

    fun addPengeluaran(name:String, codeBarang: String, harga: Int, stock: Int) = repository.addPengeluaran(name, codeBarang, harga, stock)
}
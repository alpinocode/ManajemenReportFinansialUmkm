package com.example.manajemenreportfinansialumkm.ui.stock

import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

class StockViewModel(private val repository: Repository) : ViewModel(){
    val messageError = repository.messageError
    val messageSuccess = repository.messageSuccess
    val userStock = repository.userStock

    fun addStock(name:String,nameSuplier:String, nameBarang:String, codeBarang:String, keterangan:String, jumlah:String, date:String) = repository.addStock(name, nameSuplier, nameBarang, codeBarang, keterangan, jumlah, date )
    fun getStockById(id:String) = repository.getStockById(id)

    fun updateStock(id:String,nameSuplier:String, nameBarang:String, keterangan:String, jumlah:String, date:String) = repository.updateStockData(id, nameSuplier, nameBarang, keterangan, jumlah, date)

}
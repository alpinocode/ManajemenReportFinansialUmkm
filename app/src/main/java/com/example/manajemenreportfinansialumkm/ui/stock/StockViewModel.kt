package com.example.manajemenreportfinansialumkm.ui.stock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

class StockViewModel(private val repository: Repository) : ViewModel(){
    val messageError = repository.userStockMessageError
    val messageSuccess = repository.userStockMessageSuccess
    val userStock = repository.userStock
    val isloading = repository.isLoading
    val dataUserPengeluaran = repository.userPengeluaranList

    fun addStock( name:String, nameSuplier:String, nameBarang:String, codeBarang:String, hargaJual:Int, hargaBeli:Int, stock:Int, keterangan: String,  date:String) = repository.addStock(name, nameSuplier, nameBarang, codeBarang, hargaJual, hargaBeli, stock, keterangan, date)
    
    fun getStockById(id:String) = repository.getStockById(id)

    fun updateStock(id:String,nameSuplier:String, nameBarang:String, hargaJual: Int, hargaBeli: Int, stock: Int, keterangan:String, modal: Int, date:String) = repository.updateStockData(id, nameSuplier, nameBarang, hargaJual, hargaBeli, stock, keterangan, modal, date)

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPengeluaran(name:String, codeBarang: String,hargaBeli: Int, stock: Int) = repository.addPengeluaran(name, codeBarang, hargaBeli, stock)
    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePengeluaran(id:String, modal: Int) = repository.updatePengeluaran(id, modal)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPengeluaran(id:String, date: String) = repository.getPengeluaranById(id, date)
}
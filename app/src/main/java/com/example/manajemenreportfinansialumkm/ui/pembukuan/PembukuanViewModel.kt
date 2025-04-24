package com.example.manajemenreportfinansialumkm.ui.pembukuan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

@RequiresApi(Build.VERSION_CODES.O)
class PembukuanViewModel(private val repository: Repository):ViewModel() {
    val isLoading = repository.isLoading
    val userPemasukan = repository.userPemasukanList
    val userPengeluaran = repository.userPengeluaranList

    init {
        getPemasukan()
        getPengeluaran()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPemasukan() = repository.getPemasukan()
    @RequiresApi(Build.VERSION_CODES.O)
    fun getPengeluaran() = repository.getPengeluaran()

}
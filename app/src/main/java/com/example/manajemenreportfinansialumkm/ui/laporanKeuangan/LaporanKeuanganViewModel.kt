package com.example.manajemenreportfinansialumkm.ui.laporanKeuangan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.manajemenreportfinansialumkm.data.Repository

@RequiresApi(Build.VERSION_CODES.O)
class LaporanKeuanganViewModel(private val repository: Repository) : ViewModel() {
    val isLoading = repository.isLoading
    val userPemasukan = repository.userPemasukan
    val userPengeluaran = repository.userPengeluaran

    init {
        getPemasukan()
        getPengeluaran()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPemasukan() = repository.getPemasukan()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPengeluaran() = repository.getPengeluaran()

}
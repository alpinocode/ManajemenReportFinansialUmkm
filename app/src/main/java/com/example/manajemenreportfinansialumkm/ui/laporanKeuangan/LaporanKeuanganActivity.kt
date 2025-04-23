package com.example.manajemenreportfinansialumkm.ui.laporanKeuangan

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityLaporanKeuanganBinding
import com.example.manajemenreportfinansialumkm.helper.currencyToRupiah
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class LaporanKeuanganActivity : AppCompatActivity() {
    private var binding:ActivityLaporanKeuanganBinding? = null
    private var hargaPemasukan: Int? = 0
    private var hargaPengeluaran:Int? = 0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanKeuanganBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val factory:ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel:LaporanKeuanganViewModel by viewModels {
            factory
        }





        viewModel.userPemasukan.observe(this) {
            hargaPemasukan = it?.toInt() ?: 0

            binding?.textPemasukan?.text = hargaPemasukan?.let {
                currencyToRupiah(it.toDouble())
            }
            calculateTask()
        }

        viewModel.userPengeluaran.observe(this){
            hargaPengeluaran = it?.toInt() ?: 0
            binding?.textPengeluaran?.text = hargaPengeluaran?.let { it1 -> currencyToRupiah(it1.toDouble()) }
            calculateTask()
        }

        viewModel.isLoading.observe(this) {
            if (it != null) {
                showLoading(it)
            }
        }

    }





    private fun calculateTask() {
        var saldo:Int? = 0

        saldo = hargaPemasukan.toString().toInt() - hargaPengeluaran.toString().toInt()
        Log.d("Saldo", "Pemasukan: $hargaPemasukan, pengeluaran: $hargaPengeluaran, saldo: $saldo")
        Log.d("Saldo", "Saldo: $saldo")
        if(saldo > 0 ) {

            val totalEstimasiPajak = saldo * 0.005
            binding?.textEstimasiValue?.text = currencyToRupiah(totalEstimasiPajak)
        } else {
            binding?.textEstimasiValue?.text = "Rp. 0"
        }
    }

    private fun showLoading(isLoading:Boolean) {
        if (isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.GONE
        }
    }

}
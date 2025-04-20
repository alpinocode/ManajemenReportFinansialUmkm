package com.example.manajemenreportfinansialumkm.ui.transaction

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityDetailAddTransactionBinding
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class DetailAddTransactionActivity : AppCompatActivity() {
    private var binding:ActivityDetailAddTransactionBinding? = null
    private val factory:ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel:TrasanctionViewModel by viewModels {
        factory
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val stockId = intent.getStringExtra(STOCK_ID)
        if (stockId != null) {
            viewModel.getById(stockId)

            viewModel.userTransaction.observe(this) {
                showData(it)
            }

            binding?.btnAddPemasukan?.setOnClickListener {
                val codeBarang = binding?.textCodeBarang?.text.toString()
                val harga = cleanCurrency(binding?.textHargaBarang?.text.toString())
                val stock = binding?.textInputQuantity?.text.toString().toInt()
                viewModel.addPemasukan( stockId,codeBarang, harga, stock)
                viewModel.updateStock(stockId, stock)
            }
        }
    }

    private fun showData(stock:List<Stock>) {
        binding?.textCodeBarang?.text = stock.first().codeBarang
        binding?.textNamaBarang?.text = stock.first().nameBarang
        binding?.textStockBarang?.text = stock.first().stock.toString()
        binding?.textHargaBarang?.text = formatToRupiah(stock.first().harga.toString().toDouble())
    }

    private fun formatToRupiah(number: Double):String {
        val localeID = Locale("in", "ID")
        return NumberFormat.getCurrencyInstance(localeID).format(number).replace(",00", "")
    }
    private fun cleanCurrency(text:String):Int {
        return text.replace("Rp", "", ignoreCase = true).replace(".", "")
            .replace(",", "").toInt()
    }
    companion object {
        const val STOCK_ID = "stock_id"
    }
}
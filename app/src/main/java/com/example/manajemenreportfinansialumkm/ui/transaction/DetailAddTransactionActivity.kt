package com.example.manajemenreportfinansialumkm.ui.transaction

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.databinding.ActivityDetailAddTransactionBinding
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.helper.currencyToRupiah
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import java.text.NumberFormat
import java.time.LocalDate
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
                val harga = it.first().hargaJual
                binding?.btnAddPemasukan?.setOnClickListener {
                    val codeBarang = binding?.textCodeBarang?.text.toString()
                    val stock = binding?.textInputQuantity?.text.toString().toInt()
                    val namaBarang = binding?.textNamaBarang?.text.toString()
                    harga?.let { it1 -> viewModel.addPemasukan(codeBarang, it1, stock) }
                    viewModel.updateStock(stockId, stock)

                    val date = LocalDate.now().toString()
                    if (harga != null) {
                        viewModel.addTransaction(stockId, date,namaBarang, harga, stock)
                    }

                    viewModel.messageSuccess.observe(this){
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AddTransactionActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

        }
    }

    private fun formatToRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        return NumberFormat.getCurrencyInstance(localeID).format(number).replace(",00", "")
    }
    private fun showData(stock:List<Stock>) {
        binding?.textCodeBarang?.text = stock.first().codeBarang
        binding?.textNamaBarang?.text = stock.first().nameBarang
        binding?.textStockBarang?.text = stock.first().stock.toString()
        binding?.textHargaBarang?.text = formatToRupiah(stock.first().hargaJual.toString().toDouble())
    }

    private fun cleanCurrency(text:String):Int {
        return text.replace("Rp ", "", ignoreCase = true).replace(".", "")
            .replace(",", "").toInt()
    }
    companion object {
        const val STOCK_ID = "stock_id"
    }
}
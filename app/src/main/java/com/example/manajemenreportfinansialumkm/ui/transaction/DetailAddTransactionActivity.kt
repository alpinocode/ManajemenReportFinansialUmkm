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
import java.time.LocalDate

class DetailAddTransactionActivity : AppCompatActivity() {
    private var binding:ActivityDetailAddTransactionBinding? = null
    private val factory:ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel:TrasanctionViewModel by viewModels {
        factory
    }
    private var hargaJual:Int = 0
    private var stock:Int = 0

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
                stock = binding?.textInputQuantity?.text.toString().toInt()
                val namaBarang = binding?.textNamaBarang?.text.toString()
                val date = LocalDate.now().toString()
                viewModel.addOrder(stockId, date,namaBarang, hargaJual, stock)
                viewModel.messageSuccess.observe(this){
                    if(it != null) {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AddTransactionActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
                viewModel.messageError.observe(this) {
                    if(it != null) {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }
    }

    private fun showData(stockItem:List<Stock>) {
        binding?.textCodeBarang?.text = stockItem.first().codeBarang
        binding?.textNamaBarang?.text = stockItem.first().nameBarang
        binding?.textStockBarang?.text = stockItem.first().stock.toString()
        binding?.textHargaBarang?.text = currencyToRupiah(stockItem.first().hargaJual.toString().toDouble())
        hargaJual = stockItem.first().hargaJual ?: 0
    }

    private fun cleanCurrency(text: String): Int {
        return text.replace("Rp", "", ignoreCase = true)
            .replace(".", "")
            .replace(",", "")
            .toIntOrNull() ?: 0
    }
    companion object {
        const val STOCK_ID = "stock_id"
    }
}
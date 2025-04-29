package com.example.manajemenreportfinansialumkm.ui.notification

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityUpdateStockNotifBinding
import com.example.manajemenreportfinansialumkm.helper.currencyToRupiah
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory

class UpdateStockNotifActivity : AppCompatActivity() {
    private var binding:ActivityUpdateStockNotifBinding? = null
    private var codeBarang:String = ""
    private var dateBarang:String? = ""
    private var stock:Int = 0
    private var hargaBeli:Int = 0
    private var updateModal:Int = 0
    private val factory:ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel:NotificationViewModel by viewModels {
        factory
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateStockNotifBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()



        codeBarang = intent.getStringExtra("stock_id").toString()

        var modal = 0
        binding?.btnBack?.setOnClickListener {
            Log.d("UpdateStockNotifActivity", "Btn Back Di Tekan")
            finish()
        }

        if(codeBarang != null) {
            viewModel.getStockById(codeBarang)
            viewModel.userStockData.observe(this) {
                binding?.textNamaBarang?.text = it[0].nameBarang.toString()
                binding?.textCodeBarang?.text = it[0].codeBarang.toString()
                stock = it[0].stock.toString().toInt()
                binding?.textStockBarang?.text = stock.toString()
                binding?.textHargaBarang?.text = currencyToRupiah( it[0].hargaJual.toString().toDouble())
                hargaBeli = it[0].hargaBeli.toString().toInt()
                dateBarang = it[0].date.toString()
                modal = it[0].modal.toString().toInt()
            }


            binding?.btnUpdateStock?.setOnClickListener {
                val dataStock = binding?.edtStock?.text.toString().toInt()
                val updateStock = stock + dataStock
                val updatePengeluaran = dataStock * hargaBeli
                updateModal = modal + updatePengeluaran
                viewModel.updateStock(codeBarang, updateStock, updateModal)
                viewModel.messageSuccess.observe(this) {
                    viewModel.updatePengeluaran(codeBarang, updateModal)

                    if (it != null) {
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        finish()
                        viewModel.getNotification()
                    }
                }
            }
        }


    }



    companion object {
        val STOCK_ID = "stock_id"
    }
}
package com.example.manajemenreportfinansialumkm.ui.stock

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.databinding.ActivityAddStockBinding
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

class AddStockActivity : AppCompatActivity() {
    private var binding: ActivityAddStockBinding? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStockBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val factory:ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel:StockViewModel by viewModels {
            factory
        }


        val stockId = intent.getStringExtra(STOCK_ID)
        if (stockId != null) {

            viewModel.getStockById(stockId)

            viewModel.userStock.observe(this) {
                it.also {
                    binding?.edtNamaSuplier?.setText(it[0].nameSuplier)
                    binding?.edtNamaBarang?.setText(it[0].nameBarang)
                    binding?.edtCodeBarang?.setText(it[0].codeBarang)
                    binding?.edtKeterangan?.setText(it[0].keterangan)
                    binding?.edtJumlah?.setText(it[0].jumlah)
                }
            }

            val btnUpdateStock = binding?.btnTambahStock
            btnUpdateStock?.text = "Update Stock"



            btnUpdateStock?.setOnClickListener{
                val namaBarang = binding?.edtNamaBarang?.text.toString()
                val namaSuplier = binding?.edtNamaSuplier?.text.toString()
                val keterangan = binding?.edtKeterangan?.text.toString()
                val jumlah = binding?.edtJumlah?.text.toString()
                val date = LocalDate.now().toString()

                viewModel.updateStock(stockId, namaSuplier, namaBarang, keterangan,jumlah,date)
                viewModel.messageError.observe(this) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
                viewModel.messageSuccess.observe(this) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

        } else {
            binding?.btnTambahStock?.setOnClickListener {
                val auth = FirebaseAuth.getInstance()
                val userName = auth.currentUser?.displayName.toString()

                val namaStock = binding?.edtNamaSuplier?.text.toString()
                val namaBarang = binding?.edtNamaBarang?.text.toString()
                val codeBarang = binding?.edtCodeBarang?.text.toString()
                val keterangan = binding?.edtKeterangan?.text.toString()
                val jumlah = binding?.edtJumlah?.text.toString()
                val date = LocalDate.now().toString()

                viewModel.addStock(userName, namaStock, namaBarang, codeBarang, keterangan, jumlah, date)
                viewModel.messageError.observe(this) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
                viewModel.messageSuccess.observe(this) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }

    companion object {
        private const val TAG = "AddStockActivity"
        const val STOCK_ID = "stock_id"
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}
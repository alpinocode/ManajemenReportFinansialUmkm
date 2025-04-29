package com.example.manajemenreportfinansialumkm.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityAddTransactionBinding
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.ui.adapter.ItemCardStockAdapter
import com.example.manajemenreportfinansialumkm.ui.home.HomeActivity
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddTransactionBinding
    private val factory:ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel:TrasanctionViewModel by viewModels {
        factory
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        searchTransaksion()

        binding.btnBackAddTransaction.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        viewModel.userSearchTransaction.observe(this){
            showDataTransaction(it as List<Stock>)
        }

        viewModel.userTransaction.observe(this) {
            showDataEmpty(it)
           showDataTransaction(it)
        }
        viewModel.isLoading.observe(this) {
            if(it != null) {
                showLoading(it)
            }
        }
    }

    private fun showDataTransaction(transaction:List<Stock>) {
        val adapter = ItemCardStockAdapter(transaction)
        binding.rvAddTransaksi.layoutManager = LinearLayoutManager(this)
        binding.rvAddTransaksi.adapter =adapter
        adapter.submitList(transaction)

        adapter.setOnItemClickCallback(object : ItemCardStockAdapter.OnItemClickCallback{
            override fun onItemCallback(data: Stock) {
                if(data.stock.toString().toInt() <= 0) {
                    AlertDialog.Builder(this@AddTransactionActivity).apply {
                        setTitle("Umkm Finansial Report")
                        setIcon(R.drawable.logo)
                        setMessage("Stock Barang Habis Silahkan Tambah Stock")
                        setPositiveButton("Ya", null)
                    }.show()
                } else {
                    val intent = Intent(this@AddTransactionActivity, DetailAddTransactionActivity::class.java)
                    intent.putExtra(DetailAddTransactionActivity.STOCK_ID, data.codeBarang)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        })

    }

    private fun searchTransaksion() {
        binding.searchAddTransaction.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null) {
                    viewModel.searchTransaction(query)
                    return false
                } else {
                    viewModel.loadDataTransaction()
                    return false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null) {
                    viewModel.searchTransaction(newText)
                    return false
                } else {
                    viewModel.loadDataTransaction()
                    return false
                }
            }

        })
    }
    private fun showLoading(isLoading:Boolean) {
        if (isLoading) {
            binding.progressBarTransaction.visibility = View.VISIBLE
        } else {
            binding.progressBarTransaction.visibility = View.GONE
        }
    }

    private fun showDataEmpty(stock:List<Stock>) {
        if(stock.isEmpty()) {
            binding.textStockBarangEmpty.visibility = View.VISIBLE
            binding.lottieAnimationBarangEmpty.visibility = View.VISIBLE
        } else {
            binding.textStockBarangEmpty.visibility = View.GONE
            binding.lottieAnimationBarangEmpty.visibility = View.GONE
            binding.rvAddTransaksi.visibility = View.VISIBLE
            binding.searchAddTransaction.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
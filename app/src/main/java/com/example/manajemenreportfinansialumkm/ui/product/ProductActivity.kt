package com.example.manajemenreportfinansialumkm.ui.product

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityProductBinding
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.ui.adapter.StockAdapter
import com.example.manajemenreportfinansialumkm.ui.stock.AddStockActivity
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory

class ProductActivity : AppCompatActivity() {
    private var binding: ActivityProductBinding? = null
    private val factory:ViewModelFactory  = ViewModelFactory.getInstance(this)
    private val viewModel:ProductViewModel by viewModels {
        factory
    }
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        val adapter = StockAdapter()
        binding?.rvStock?.layoutManager = LinearLayoutManager(this)
        binding?.rvStock?.adapter = adapter



        adapter.onDeleteClick = {
            AlertDialog.Builder(this).apply {
                setTitle("Konfirmasi Hapus")
                setMessage("Yakin ingin menghapus ?")
                setPositiveButton("Ya") { _, _ ->
                    viewModel.deleteStock(it)  // implementasikan di ViewModel
                }
                setNegativeButton("Batal", null)
            }.show()
            viewModel.messageSuccess.observe(this) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
            viewModel.messageError.observe(this) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Aksi untuk tambah stok
        binding?.floatingActionAddStock?.setOnClickListener {
            val intent = Intent(this, AddStockActivity::class.java)
            startActivity(intent)
        }


        viewModel.userStock.observe(this) {
            adapter.submitList(it as MutableList<Stock>?)
        }

        viewModel.isLoading.observe(this) {
            if (it != null) {
                showLoading(it)
            }
        }



    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.progressbar?.visibility = View.VISIBLE
        } else {
            binding?.progressbar?.visibility = View.GONE
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.loadStockInData()
    }
}
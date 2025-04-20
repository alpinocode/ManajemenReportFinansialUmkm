package com.example.manajemenreportfinansialumkm.ui.stock

import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityAddStockBinding
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*

class AddStockActivity : AppCompatActivity() {
    private var binding: ActivityAddStockBinding? = null
    private var originalCodeBarang: String? = null
    private val REQUEST_CODE_PICK_IMAGE = 100
    private var imageUrl: Uri? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStockBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.setHomeButtonEnabled(true)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: StockViewModel by viewModels {
            factory
        }

        val spinnerCategory = binding?.itemKategoryProduct

        ArrayAdapter.createFromResource(
            this,
            R.array.kategory_product_selected,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinnerCategory?.adapter = adapter
        }

        var dataCategory: String? = null
        spinnerCategory?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                dataCategory = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action
            }
        }


        setupHargaFormat()

        val stockId = intent.getStringExtra(STOCK_ID)

        if (stockId != null) {
            viewModel.getStockById(stockId)

            viewModel.userStock.observe(this) {
                it.also {
                    binding?.edtNamaSuplier?.setText(it[0].nameSuplier)
                    binding?.edtNamaBarang?.setText(it[0].nameBarang)
                    binding?.edtCodeBarang?.setText(it[0].codeBarang)
                    binding?.edtKeterangan?.setText(it[0].keterangan)
                    binding?.edtStock?.setText(it[0].stock.toString())
                    binding?.edtHarga?.setText(formatToRupiah(it[0].harga.toString().toDouble()))

                    originalCodeBarang = it[0].codeBarang

                    val kategori = it[0].kategory
                    val adapter = spinnerCategory?.adapter as ArrayAdapter<String>
                    val spinnerPosition = adapter.getPosition(kategori)
                    spinnerCategory.setSelection(spinnerPosition)
                    dataCategory = kategori

                    var isFirstSetText = true
                    binding?.edtCodeBarang?.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            if (isFirstSetText) {
                                isFirstSetText = false
                                return
                            }

                            val newCode = s.toString()
                            if (originalCodeBarang != null && newCode != originalCodeBarang) {
                                showCodeChangeAlert()
                                originalCodeBarang = null
                            }
                        }
                    })
                }
            }

            val btnUpdateStock = binding?.btnTambahStock
            btnUpdateStock?.text = "Update Stock"

            btnUpdateStock?.setOnClickListener {
                val namaSuplier = binding?.edtNamaSuplier?.text.toString()
                val namaBarang = binding?.edtNamaBarang?.text.toString()
                val keterangan = binding?.edtKeterangan?.text.toString()
                val stock = binding?.edtStock?.text.toString().toInt()
                val harga = cleanCurrency(binding?.edtHarga?.text.toString())
                val date = LocalDate.now().toString()

                viewModel.updateStock(stockId, namaSuplier, namaBarang, dataCategory.toString(), stock, harga, keterangan, date)
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

                val namaSuplier = binding?.edtNamaSuplier?.text.toString()
                val namaBarang = binding?.edtNamaBarang?.text.toString()
                val codeBarang = binding?.edtCodeBarang?.text.toString()
                val stock = binding?.edtStock?.text.toString().toInt()
                val harga = cleanCurrency(binding?.edtHarga?.text.toString())
                val keterangan = binding?.edtKeterangan?.text.toString()
                val date = LocalDate.now().toString()

                viewModel.addStock( userName, namaSuplier, namaBarang, codeBarang, dataCategory.toString(), harga, stock, keterangan, date)
                viewModel.addPengeluaran(userName, codeBarang, harga, stock)

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

    private fun setupHargaFormat() {
        val localeID = Locale("in", "ID")
        val hargaEditText = binding?.edtHarga
        var currentText = ""

        hargaEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != currentText) {
                    hargaEditText.removeTextChangedListener(this)

                    val cleanString = s.toString()
                        .replace("Rp", "", ignoreCase = true)
                        .replace(".", "")
                        .replace(",", "")
                        .replace(" ", "")

                    if (cleanString.isNotEmpty()) {
                        try {
                            val parsed = cleanString.toDouble()
                            val formatted = NumberFormat.getCurrencyInstance(localeID).format(parsed).replace(",00", "")
                            currentText = formatted
                            hargaEditText.setText(formatted)
                            hargaEditText.setSelection(formatted.length)
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                    }

                    hargaEditText.addTextChangedListener(this)
                }
            }
        })
    }

    private fun cleanCurrency(text: String): Int {
        return text.replace("Rp", "", ignoreCase = true)
            .replace(".", "")
            .replace(",", "")
            .toInt()
    }

    private fun formatToRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        return NumberFormat.getCurrencyInstance(localeID).format(number).replace(",00", "")
    }

    private fun showCodeChangeAlert() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Kode Barang Berubah")
            .setMessage("Apakah kamu yakin ingin mengubah kode barang?")
            .setPositiveButton("Lanjut") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
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

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
import java.time.format.DateTimeFormatter
import java.util.*

class AddStockActivity : AppCompatActivity() {
    private var binding: ActivityAddStockBinding? = null
    private var originalCodeBarang: String? = null
    private val REQUEST_CODE_PICK_IMAGE = 100
    private var imageUrl: Uri? = null
    private var dataStock:Int = 0
    private var dataSTockFromDb: Int = 0
    private var orginalStockFromDb:Int = 0
    private var modal:Int = 0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStockBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.setHomeButtonEnabled(true)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(baseContext)
        val viewModel: StockViewModel by viewModels {
            factory
        }



        setupModalFormat()
        setupHargaFormat()

        val stockId = intent.getStringExtra(STOCK_ID)
        dataStock = savedInstanceState?.getInt("stock") ?: 0
        dataSTockFromDb = savedInstanceState?.getInt("stockFromDb") ?: 0

        if (stockId != null) {
            viewModel.getStockById(stockId)
            viewModel.userStock.observe(this) {
                it.also {
                    orginalStockFromDb = it[0].stock.toString().toInt()
                    dataSTockFromDb = orginalStockFromDb
                    binding?.edtNamaSuplier?.setText(it[0].nameSuplier)
                    binding?.edtNamaBarang?.setText(it[0].nameBarang)
                    binding?.edtCodeBarang?.setText(it[0].codeBarang)
                    binding?.edtKeterangan?.setText(it[0].keterangan)
                    binding?.textJumlahStock?.setText(dataSTockFromDb.toString())
                    binding?.edtHargaBeli?.setText(formatToRupiah(it[0].hargaBeli.toString().toDouble()))
                    binding?.edtHargaJual?.setText(formatToRupiah(it[0].hargaJual.toString().toDouble()))
                    viewModel.getPengeluaran(stockId, it[0].date.toString())

                    originalCodeBarang = it[0].codeBarang


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
            viewModel.isloading.observe(this){
                if (it != null) {
                    showLoading(it)
                }
            }

            val btnUpdateStock = binding?.btnTambahStock
            btnUpdateStock?.text = "Update Stock"


            viewModel.dataUserPengeluaran.observe(this) {
                modal = it?.firstOrNull()?.totalPengeluaran?.toInt() ?: 0
                Log.d(TAG, "Cek Datanya Sih Modal :${modal}")
            }


            binding?.btnAddStock?.setOnClickListener {
                dataSTockFromDb++
                binding?.textJumlahStock?.text = dataSTockFromDb.toString()
            }
            binding?.btnKurangiStock?.setOnClickListener {
                dataSTockFromDb--
                binding?.textJumlahStock?.text = dataSTockFromDb.toString()
            }

            btnUpdateStock?.setOnClickListener {
                val namaSuplier = binding?.edtNamaSuplier?.text.toString()
                val namaBarang = binding?.edtNamaBarang?.text.toString()
                val keterangan = binding?.edtKeterangan?.text.toString()
                val dataStockBaru = binding?.textJumlahStock?.text.toString().toInt()
                val hargaJual = cleanCurrency(binding?.edtHargaJual?.text.toString())
                val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM", Locale("id", "ID"))).toString()
                val hargaBeli = cleanCurrency(binding?.edtHargaBeli?.text.toString())

                val selisihStock = dataStockBaru - orginalStockFromDb
                val absSelisihStock = kotlin.math.abs(selisihStock)

                if (dataStockBaru > orginalStockFromDb) {
                    val dataUpdate = hargaBeli * absSelisihStock
                    val dataStockAdd = modal + dataUpdate
                    val stock = orginalStockFromDb + absSelisihStock

                    viewModel.updatePengeluaran(stockId, dataStockAdd)
                    viewModel.updateStock(stockId, namaSuplier, namaBarang, hargaJual, hargaBeli, stock, keterangan, dataStockAdd, date)
                    viewModel.messageError.observe(this) {
                        if(it != null) {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

                        }
                    }
                    viewModel.messageSuccess.observe(this) {
                        if(it != null) {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            finish()
                        }

                    }
                } else if (dataStockBaru < orginalStockFromDb) {
                    val dataUpdateUntukKurangiModal = hargaBeli * absSelisihStock
                    val dataModalYangSudahDiKurangi = modal - dataUpdateUntukKurangiModal
                    val stock = orginalStockFromDb - absSelisihStock

                    viewModel.updateStock(stockId, namaSuplier, namaBarang, hargaJual, hargaBeli, stock, keterangan, dataModalYangSudahDiKurangi, date)
                    viewModel.updatePengeluaran(stockId, dataModalYangSudahDiKurangi)
                    viewModel.messageError.observe(this) {
                        if(it != null) {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewModel.messageSuccess.observe(this) {
                        if (it != null) {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            finish()
                        }

                    }
                }

            }



        } else {
            binding?.btnAddStock?.setOnClickListener {


                dataStock++
                binding?.textJumlahStock?.text = dataStock.toString() ?: "0"
            }

            binding?.btnKurangiStock?.setOnClickListener {
                if (dataStock > 0) {
                    dataStock--
                    binding?.textJumlahStock?.text = dataStock.toString()
                } else {
                    Toast.makeText(this, "Stock Tidak Boleh Kurang Dari 0", Toast.LENGTH_SHORT).show()
                }
            }

            binding?.btnTambahStock?.setOnClickListener {
                val auth = FirebaseAuth.getInstance()
                val userName = auth.currentUser?.displayName.toString()



                val namaSuplier = binding?.edtNamaSuplier?.text.toString()
                val namaBarang = binding?.edtNamaBarang?.text.toString()
                val codeBarang = binding?.edtCodeBarang?.text.toString()
                var stock = binding?.textJumlahStock?.text.toString().toInt() ?: 0
                val hargaJual = cleanCurrency(binding?.edtHargaJual?.text.toString())
                val keterangan = binding?.edtKeterangan?.text.toString()
                val hargaBeli = cleanCurrency(binding?.edtHargaBeli?.text.toString())
                val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM", Locale("id", "ID"))).toString()



                viewModel.addStock( userName, namaSuplier, namaBarang, codeBarang, hargaJual, hargaBeli,stock, keterangan, date)
                viewModel.messageSuccess.observe(this) {
                    viewModel.addPengeluaran(userName, codeBarang, hargaBeli, stock)
                    it?.let { it1 -> showMessageSuccess(it1) }
                }
                viewModel.messageError.observe(this) {
                    it?.let { it1 -> showMessageError(it1) }
                }
            }
        }
    }

    private fun setupHargaFormat() {
        val localeID = Locale("in", "ID")
        val hargaEditText = binding?.edtHargaJual
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

    private fun setupModalFormat() {
        val localeID = Locale("in", "ID")
        val modalEditText = binding?.edtHargaBeli
        var currentModalText = ""

        modalEditText?.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(s.toString() != currentModalText) {
                    modalEditText.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("Rp", "", ignoreCase = true)
                        .replace(".", "")
                        .replace(",", "")
                        .replace("", "")

                    if(cleanString.isNotEmpty()) {
                        try {
                            val parsed = cleanString.toDouble()
                            val formatted = NumberFormat.getCurrencyInstance(localeID).format(parsed).replace(",00", "")
                            currentModalText = formatted
                            modalEditText.setText(formatted)
                            modalEditText.setSelection(formatted.length)
                        } catch (e:Exception) {
                            e.printStackTrace()
                        }
                    }
                    modalEditText.addTextChangedListener(this)
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

    private fun showMessageSuccess(message:String) {
        Toast.makeText(this, message?:"Terjadi kesalahan", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showMessageError(message:String) {
        Toast.makeText(this, message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()

    }

    private fun showLoading(isLoading:Boolean) {
        binding?.progressBar?.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "AddStockActivity"
        const val STOCK_ID = "stock_id"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("stock", dataStock)
        outState.putInt("stockFromDb", dataSTockFromDb)
        super.onSaveInstanceState(outState)
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}

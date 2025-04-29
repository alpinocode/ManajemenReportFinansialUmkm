package com.example.manajemenreportfinansialumkm.ui.laporanKeuangan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.ActivityLaporanKeuanganBinding
import com.example.manajemenreportfinansialumkm.helper.currencyToRupiah
import com.example.manajemenreportfinansialumkm.ui.home.HomeActivity
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF

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

        binding?.btnBackFinansialReport?.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        viewModel.userPemasukan.observe(this) {
            hargaPemasukan = it?.toInt() ?: 0

            binding?.textPemasukan?.text = hargaPemasukan?.let {
                currencyToRupiah(it.toDouble())
            }
            calculateTask()

            dataPieChar()
        }

        viewModel.userPengeluaran.observe(this){
            hargaPengeluaran = it?.toInt() ?: 0
            binding?.textPengeluaran?.text = hargaPengeluaran?.let { it1 -> currencyToRupiah(it1.toDouble()) }
            calculateTask()
            dataPieChar()
        }

        viewModel.isLoading.observe(this) {
            if (it != null) {
                showLoading(it)
            }
        }



    }

    private fun calculateTask() {
        val dataMentahan = hargaPemasukan.toString().toInt() - hargaPengeluaran.toString().toInt()
        val confertDataTax = if(dataMentahan < 0 ) 0 else dataMentahan * 0.05
        val dataLabaBersih = if(dataMentahan < 0 ) 0 else dataMentahan
        binding?.textLabaBersih?.text = currencyToRupiah(dataLabaBersih.toString().toDouble())
        binding?.textPajak?.text = currencyToRupiah(confertDataTax.toString().toDouble())
    }

    private fun dataPieChar(){
        val pieChart = binding?.pieChart
        pieChart?.setUsePercentValues(true)
        pieChart?.getDescription()?.setEnabled(false)
        pieChart?.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart?.setDragDecelerationFrictionCoef(0.95f)

        pieChart?.setDrawHoleEnabled(true)
        pieChart?.setHoleColor(Color.WHITE)

        pieChart?.setTransparentCircleColor(Color.WHITE)
        pieChart?.setTransparentCircleAlpha(110)

        pieChart?.setHoleRadius(58f)
        pieChart?.setTransparentCircleRadius(61f)

        pieChart?.setDrawCenterText(true)

        pieChart?.setRotationAngle(0f)

        pieChart?.setRotationEnabled(true)
        pieChart?.setHighlightPerTapEnabled(true)

        pieChart?.animateY(1400, Easing.EaseInOutQuad)

        pieChart?.legend?.isEnabled = false
        pieChart?.setEntryLabelColor(Color.WHITE)
        pieChart?.setEntryLabelTextSize(12f)

        val dataMentahan = hargaPemasukan.toString().toInt() - hargaPengeluaran.toString().toInt()
        val dataLabaBersih = if(dataMentahan < 0 ) 0 else dataMentahan
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(hargaPemasukan.toString().toFloat()))
        entries.add(PieEntry(dataLabaBersih.toFloat()))
        entries.add(PieEntry(hargaPengeluaran.toString().toFloat()))

        val dataSet = PieDataSet(entries, "Mobile OS")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.red))

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart?.setData(data)

        pieChart?.highlightValues(null)

        pieChart?.invalidate()
    }

    private fun showLoading(isLoading:Boolean) {
        if (isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.GONE
        }
    }

}
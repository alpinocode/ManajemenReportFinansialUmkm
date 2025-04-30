package com.example.manajemenreportfinansialumkm.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manajemenreportfinansialumkm.databinding.ActivityHistoryTransactionBinding
import com.example.manajemenreportfinansialumkm.helper.Transaction
import com.example.manajemenreportfinansialumkm.ui.adapter.TrasanctionAdapter
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory

class HistoryTransactionActivity : AppCompatActivity() {
    private var binding: ActivityHistoryTransactionBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryTransactionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val factory:ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel:TransactionViewModel by viewModels {
            factory
        }

        val adapter = TrasanctionAdapter()
        binding?.rvHistoryTransaction?.layoutManager = LinearLayoutManager(this)
        binding?.rvHistoryTransaction?.adapter = adapter

        binding?.btnBackHistoryTransaction?.setOnClickListener {
            finish()
        }

        viewModel.userHistoryTransaction.observe(this) {
            if(it != null) {
                showAndHideLottieAnimation(it)
                adapter.submitList(it)
            }
        }
        viewModel.isLoading.observe(this) {
            if(it != null) {
                showLoading(it)
            }
        }
    }

    private fun showLoading(isLoading:Boolean) {
        if(isLoading) {
            binding?.progressBarTransaction?.visibility = View.VISIBLE
        } else {
            binding?.progressBarTransaction?.visibility = View.GONE
        }
    }

    private fun showAndHideLottieAnimation(transaction:List<Transaction>) {
        if(transaction.isEmpty()) {
            binding?.textDataTransactionEmpty?.visibility = View.VISIBLE
            binding?.lottieAnimationDataTransactionEmpty?.visibility = View.VISIBLE
        } else {
            binding?.textDataTransactionEmpty?.visibility = View.GONE
            binding?.lottieAnimationDataTransactionEmpty?.visibility = View.GONE
        }
    }
}
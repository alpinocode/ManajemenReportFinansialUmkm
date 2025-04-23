package com.example.manajemenreportfinansialumkm.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenreportfinansialumkm.databinding.ItemTransactionHistoryBinding
import com.example.manajemenreportfinansialumkm.helper.Transaction
import com.example.manajemenreportfinansialumkm.helper.currencyToRupiah

class TrasanctionAdapter : ListAdapter<Transaction, TrasanctionAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding:ItemTransactionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.codeBarangTransactinHistory.text = transaction.id
            binding.tanggalBarangTransactionHistory.text = transaction.date
            binding.namaBarangTransactionHistory.text = transaction.namaBarang
            binding.stockBarangTransactionHistory.text = transaction.quantity.toString()
            binding.hargaBarangTransactionHistory.text = transaction.totalHarga?.toDouble()
                ?.let { currencyToRupiah(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemView: Int): MyViewHolder {
        val  binding = ItemTransactionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }

        }
    }
}
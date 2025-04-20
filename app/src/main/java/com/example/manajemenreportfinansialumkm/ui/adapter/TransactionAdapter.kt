package com.example.manajemenreportfinansialumkm.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenreportfinansialumkm.databinding.TransactionItemBinding
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.ui.transaction.DetailAddTransactionActivity

class TransactionAdapter : ListAdapter<Stock, TransactionAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: TransactionItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(stock: Stock) {
            binding.namaBarang.text =stock.nameBarang
            binding.codeBarang.text = stock.codeBarang.toString()
            binding.stockBarang.text = stock.stock.toString()

            binding.trasanctionCard.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetailAddTransactionActivity::class.java)
                intent.putExtra(DetailAddTransactionActivity.STOCK_ID, stock.codeBarang)
                context.startActivity(intent)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val getItem = getItem(position)
        holder.bind(getItem)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem.codeBarang == newItem.codeBarang
            }

            override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem == newItem
            }

        }
    }

}
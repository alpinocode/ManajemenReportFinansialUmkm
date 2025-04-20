package com.example.manajemenreportfinansialumkm.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenreportfinansialumkm.databinding.ItemStockBinding
import com.example.manajemenreportfinansialumkm.databinding.NotificationItemBinding
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.ui.stock.AddStockActivity

class NotificationAdapter : ListAdapter<Stock,NotificationAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: NotificationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: Stock) {
            binding.apply {
                namaBarang.text = stock.nameBarang
                codeBarang.text = stock.codeBarang
                stockBarang.text = stock.stock.toString()
                notificationCard.setOnClickListener {
                    val context = binding.root.context
                    val intent = Intent(context, AddStockActivity::class.java)
                    intent.putExtra(AddStockActivity.STOCK_ID, stock.codeBarang)
                    context.startActivity(intent)
                }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stock>(){
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem.codeBarang == newItem.codeBarang
            }

            override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem == newItem
            }

        }
    }
}
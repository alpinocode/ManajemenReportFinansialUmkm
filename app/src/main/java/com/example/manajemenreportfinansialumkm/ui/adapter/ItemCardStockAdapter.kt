package com.example.manajemenreportfinansialumkm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenreportfinansialumkm.databinding.ItemCardStockBinding
import com.example.manajemenreportfinansialumkm.helper.Stock

class ItemCardStockAdapter(private val stockItem:List<Stock>) : ListAdapter<Stock,ItemCardStockAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback:OnItemClickCallback

    fun setOnItemClickCallback(onItemCallback:OnItemClickCallback) {
        this.onItemClickCallback = onItemCallback
    }
    class MyViewHolder(private val binding: ItemCardStockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: Stock) {
            binding.apply {
                namaBarang.text = stock.nameBarang
                codeBarang.text = stock.codeBarang
                stockBarang.text = stock.stock.toString()
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClickCallback.onItemCallback(stockItem[position]) }
    }

    interface OnItemClickCallback {
        fun onItemCallback(data:Stock)
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
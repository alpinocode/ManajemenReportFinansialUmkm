package com.example.manajemenreportfinansialumkm.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.manajemenreportfinansialumkm.databinding.ItemStockBinding
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.ui.stock.AddStockActivity
import com.example.manajemenreportfinansialumkm.ui.stock.StockViewModel
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory

class StockAdapter() : ListAdapter<Stock, StockAdapter.MyViewHolder>(DIFF_CALLBACK) {

    var onDeleteClick: ((id:String) -> Unit)? = null
    inner class MyViewHolder(private val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: Stock, position: Int) {
            binding.apply {
                val context = binding.root.context
                nomor.text = (position + 1).toString()
                codeBarang.text = stock.codeBarang
                namaBarang.text = stock.nameBarang
                containerStock.setOnClickListener {
                    val intent = Intent(context, AddStockActivity::class.java)
                    intent.putExtra(AddStockActivity.STOCK_ID, stock.codeBarang)
                    context.startActivity(intent)
                }
                actionDeleteStock.setOnClickListener {
                    onDeleteClick?.invoke(stock.codeBarang.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stock>() {
            override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem.codeBarang == newItem.codeBarang
            }

            override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
                return oldItem == newItem
            }
        }
    }

}
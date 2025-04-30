package com.example.manajemenreportfinansialumkm.ui.notification

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manajemenreportfinansialumkm.databinding.FragmentNotificationBinding
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.ui.adapter.ItemCardStockAdapter
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory

class NotificationFragment : Fragment() {
    private lateinit var binding:FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory:ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel:NotificationViewModel by viewModels {
            factory
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        (activity as AppCompatActivity).supportActionBar?.hide()


        viewModel.notification.observe(viewLifecycleOwner) {
            showDataNotificationStock(it)
        }



        viewModel.isLoading.observe(viewLifecycleOwner){
            if (it != null) {
                showLoading(it)
            }
        }
    }

    private fun showDataNotificationStock(stock:List<Stock>) {
        val stockAdapter = ItemCardStockAdapter(stock)
        binding.rvNotification.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvNotification.adapter = stockAdapter
        stockAdapter.submitList(stock)

        stockAdapter.setOnItemClickCallback(object : ItemCardStockAdapter.OnItemClickCallback {
            override fun onItemCallback(data: Stock) {
                val intent = Intent(requireContext(), UpdateStockNotifActivity::class.java)
                intent.putExtra(UpdateStockNotifActivity.STOCK_ID, data.codeBarang)
                startActivity(intent)
            }
        })

        if(stock.isEmpty()) {
            binding.textNotificationEmpty.visibility = View.VISIBLE
            binding.lottieNotificationEmpty.visibility = View.VISIBLE
        } else {
            binding.textNotificationEmpty.visibility = View.GONE
            binding.lottieNotificationEmpty.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarNotification.visibility = View.VISIBLE
        } else {
            binding.progressBarNotification.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()

    }

}
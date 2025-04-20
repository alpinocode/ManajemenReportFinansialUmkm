package com.example.manajemenreportfinansialumkm.ui.notification

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
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.FragmentNoficationBinding
import com.example.manajemenreportfinansialumkm.ui.adapter.NotificationAdapter
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory

class NoficationFragment : Fragment() {
    private lateinit var binding:FragmentNoficationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoficationBinding.inflate(inflater, container, false)
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

        val adapter = NotificationAdapter()
        binding.rvNotification.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvNotification.adapter = adapter

        viewModel.notification.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }



        viewModel.isLoading.observe(viewLifecycleOwner){
            if (it != null) {
                showLoading(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
       if (isLoading) {
           binding.progressBarNotification.visibility = View.VISIBLE
       } else {
           binding.progressBarNotification.visibility = View.GONE
       }
    }




}
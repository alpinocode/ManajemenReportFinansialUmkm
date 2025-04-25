package com.example.manajemenreportfinansialumkm.ui.pembukuan

import PembukuanAdapter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manajemenreportfinansialumkm.databinding.FragmentPembukuanBinding
import com.example.manajemenreportfinansialumkm.helper.PembukuanItem
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory


class PembukuanFragment : Fragment() {
   private var binding: FragmentPembukuanBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPembukuanBinding.inflate(inflater, container, false)

        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val factory:ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel:PembukuanViewModel by viewModels {
            factory
        }

        val pembukuanAdapter = PembukuanAdapter()
        binding?.rvPembukuan?.layoutManager = LinearLayoutManager(requireActivity())
        binding?.rvPembukuan?.adapter = pembukuanAdapter


        viewModel.userPemasukan.observe(viewLifecycleOwner) { pemasukanList ->
            viewModel.userPengeluaran.observe(viewLifecycleOwner) { pengeluaranList ->
                val combinedList = mutableListOf<PembukuanItem>()
                pemasukanList?.forEach {
                    combinedList.add(PembukuanItem.PemasukanItem(it))
                }

                pengeluaranList?.forEach {
                    combinedList.add(PembukuanItem.PengeluaranItem(it))
                }


                if (combinedList.isNotEmpty()) {
                    binding?.lottieDataEmpty?.visibility = View.GONE
                    binding?.textDataEmpty?.visibility = View.GONE
                    pembukuanAdapter.submitList(combinedList)
                } else {
                    binding?.textDataEmpty?.visibility = View.VISIBLE
                    binding?.lottieDataEmpty?.visibility = View.VISIBLE
                }
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {

            if (it != null) {
                showLoading(it)
            }
        }



    }
    private fun showLoading(isLoading:Boolean) {
        if(isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.GONE
        }
    }

}
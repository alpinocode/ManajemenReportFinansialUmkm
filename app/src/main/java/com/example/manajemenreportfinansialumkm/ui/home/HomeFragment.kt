package com.example.manajemenreportfinansialumkm.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.FragmentHomeBinding
import com.example.manajemenreportfinansialumkm.ui.Login.LoginActivity
import com.example.manajemenreportfinansialumkm.ui.laporanKeuangan.LaporanKeuanganActivity
import com.example.manajemenreportfinansialumkm.ui.product.ProductActivity
import com.example.manajemenreportfinansialumkm.ui.transaction.AddTransactionActivity
import com.example.manajemenreportfinansialumkm.ui.transaction.HistoryTransactionActivity
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private  var usersAuth: FirebaseAuth? = null
    private var userVerification:Boolean? = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val toProduct = binding?.productContainer

        val factory:ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel:HomeViewModel by viewModels {
            factory
        }

        usersAuth = FirebaseAuth.getInstance()

        dataUserAuth(usersAuth?.currentUser)
        viewModel.userVerification.observe(viewLifecycleOwner){
            if(it == true) {
                binding?.btnVerificationEmail?.visibility = View.GONE
                userVerification = true
            } else {
                binding?.btnVerificationEmail?.setOnClickListener{
                    viewModel.sendVerificationEmail()
                    AlertDialog.Builder(requireActivity()).apply {
                        setTitle("Umkm Finansial Report")
                        setIcon(R.drawable.logo)
                        setMessage("Coba Cek Email Anda Secara Berkala")
                        setPositiveButton("Ya", null)
                    }.show()
                }
                userVerification = false
            }
        }


        toProduct?.setOnClickListener{
            Log.d(TAG, "Apakah User Udah Verification ${userVerification}")
            if (userVerification == true) {
                val intent = Intent(requireContext(), ProductActivity::class.java)
                startActivity(intent)
            } else {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Umkm Finansial Report")
                    setIcon(R.drawable.logo)
                    setMessage("Harap Verification Dulu Sebelum Tambah Barang/stock")
                    setPositiveButton("Ya", null)
                }.show()
            }


        }

        binding?.addTransaksiContainer?.setOnClickListener{
            val intent = Intent(requireContext(), AddTransactionActivity::class.java)
            startActivity(intent)
        }

        binding?.laporanKeuanganContainer?.setOnClickListener{
            val intent = Intent(requireContext(), LaporanKeuanganActivity::class.java)
            startActivity(intent)
        }

        binding?.hostyriTranksaksi?.setOnClickListener{
            val intent = Intent(requireContext(), HistoryTransactionActivity::class.java)
            startActivity(intent)
        }





        binding?.btnLogout?.setOnClickListener{
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Umkm Report Finansial")
                setIcon(R.drawable.logo)
                setMessage("Yakin Untuk Logout")
                setPositiveButton("Ya") { _, _ ->
                    viewModel.signOut(requireContext())
                    usersAuth?.signOut()
                    LoginManager.getInstance().logOut()

                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireActivity(), HomeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finishAffinity()

                }
                setNegativeButton("Tidak", null)
            }.show()

        }

      
    }




    // data Authentication Firebase Provider Authentication
    private fun dataUserAuth(usersData: FirebaseUser?) {
        if(usersData?.photoUrl == null) {
            binding?.textUsername?.text = "Hello ${usersData?.displayName}"
            binding?.imageUser?.let {
                Glide.with(requireContext())
                    .load(R.drawable.avatar)
                    .into(it)
            }
        } else {
            binding?.textUsername?.text = "Hello ${usersData?.displayName}"

            binding?.imageUser?.let {
                Glide.with(this)
                    .load(usersData.photoUrl)
                    .into(it)
            }
        }
    }

    private fun logoutButton() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        val TAG = "home_fragment"
    }
}
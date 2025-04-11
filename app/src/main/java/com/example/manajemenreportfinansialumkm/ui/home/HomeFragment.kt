package com.example.manajemenreportfinansialumkm.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.FragmentHomeBinding
import com.example.manajemenreportfinansialumkm.ui.product.ProductActivity
import com.example.manajemenreportfinansialumkm.ui.viewModelFactory.ViewModelFactory
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private  var usersAuth: FirebaseAuth? = null

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


        toProduct?.setOnClickListener{
            val intent = Intent(requireContext(), ProductActivity::class.java)
            startActivity(intent)
        }

        usersAuth = FirebaseAuth.getInstance()
        viewModel.userAuth.observe(requireActivity()) {
            if(it != null) {
                dataUserAuth(it)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                usersAuth?.currentUser?.uid?.let { viewModel.logOut(it) }
                viewModel.signOut(requireContext())
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                requireActivity().finish()
            }
        })
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


    companion object {
        val TAG = "home_fragment"
    }
}
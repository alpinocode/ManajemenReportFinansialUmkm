package com.example.manajemenreportfinansialumkm.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.SignUpAndSignOutViewModel
import com.example.manajemenreportfinansialumkm.databinding.FragmentHomeBinding
import com.example.manajemenreportfinansialumkm.ui.HomeActivity
import com.example.manajemenreportfinansialumkm.ui.product.ProductActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlin.math.log


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private  var usersAuth: FirebaseAuth? = null
    private  val signOutViewModel:SignUpAndSignOutViewModel by viewModels()

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

        toProduct?.setOnClickListener{
            val intent = Intent(requireContext(), ProductActivity::class.java)
            startActivity(intent)
        }

        usersAuth = FirebaseAuth.getInstance()

        val usersDataAuth = usersAuth?.currentUser


        if(usersDataAuth != null) {
            dataUserAuth(usersDataAuth)
        }


        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                signOutViewModel.signOut(requireContext())
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                requireActivity().finish()

            }
        })
    }




    // data Authentication Firebase Provider Authentication
    private fun dataUserAuth(usersData: FirebaseUser?) {

        if(usersData?.displayName == null) {
            val database = FirebaseDatabase.getInstance().getReference("users")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataUserId = usersAuth?.currentUser?.uid
                    val data = snapshot.child(dataUserId.toString()).child("name")
                    binding?.textUsername?.text = "Hello ${data.value}"
                    binding?.imageUser?.let {
                        Glide.with(requireContext())
                            .load(R.drawable.avatar)
                            .into(it)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Terjadi Kesalahan pada database", Toast.LENGTH_SHORT).show()
                }

            })
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
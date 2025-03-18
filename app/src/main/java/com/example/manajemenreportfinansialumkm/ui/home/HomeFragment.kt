package com.example.manajemenreportfinansialumkm.ui.home

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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.databinding.FragmentHomeBinding
import com.example.manajemenreportfinansialumkm.ui.HomeActivity
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
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        usersAuth = FirebaseAuth.getInstance()

        val usersDataAuth = usersAuth?.currentUser

        val dataUser = requireActivity().intent.getStringExtra(HomeActivity.DATA_USER)

        if(dataUser != null) {
          dataUserAuthDatabase(dataUser)
            Glide.with(this)
                .load(R.drawable.avatar)
                .into(binding!!.imageUser)
        } else if(usersDataAuth != null) {
            dataUserAuth(usersDataAuth)
        }

        credentialManager = CredentialManager.create(requireContext())


        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                signOut()
                FirebaseAuth.getInstance().signOut()
                LoginManager.getInstance().logOut()
                requireActivity().finish()

            }
        })
    }

    private fun signOut() {
        // Firebase sign out
        usersAuth?.signOut()

        // When a user signs out, clear the current user credential state from all credential providers.
        lifecycleScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                dataUserAuth(null)
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
    }

    // data Authentication Firebase Provider Authentication
    private fun dataUserAuth(usersData: FirebaseUser?) {
        binding?.textUsername?.text = "Hello ${usersData?.displayName}"

        binding?.imageUser?.let {
            Glide.with(this)
                .load(usersData?.photoUrl)
                .into(it)
        }

    }

    // data authentication firebase database
    private fun dataUserAuthDatabase(userData:String) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.child(userData).child("name")
                val dataUSerTitle = data.value.toString()
                binding?.textUsername?.text = "Hello $dataUSerTitle"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(binding?.root?.context, "Data Tidak Ada", Toast.LENGTH_SHORT).show()
            }
        })
    }


    companion object {
        val TAG = "home_fragment"
    }
}
package com.example.manajemenreportfinansialumkm.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.manajemenreportfinansialumkm.R
import com.example.manajemenreportfinansialumkm.data.local.entity.UserEntity
import com.example.manajemenreportfinansialumkm.data.local.room.UserDao
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.facebook.AccessToken
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate

class Repository(private val userDao: UserDao, private val context: Context) {
    private val _messageError = MutableLiveData<String?>()
    val messageError:LiveData<String?> = _messageError

    private val _messageSuccess = MutableLiveData<String?>()
    val messageSuccess:LiveData<String?> = _messageSuccess

    private val _isLoading = MutableLiveData<Boolean?>()
    val isLoading:LiveData<Boolean?> = _isLoading

    private val _userAuth = MutableLiveData<FirebaseUser?>()
    val userAuth:LiveData<FirebaseUser?> = _userAuth

    private val _userStock = MutableLiveData<List<Stock>>()
    val userStock:LiveData<List<Stock>> = _userStock



    val auth = FirebaseAuth.getInstance()

    fun register(name:String, email:String, password:String, confirmPassword:String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _messageError.value = "Format email tidak valid"
            return
        }
        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _messageError.value = "name, email, password, confirm password Is not Empty"
            return
        }
        if(password != confirmPassword) {
            _messageError.value = "Password and Confirm Password do not match"
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {

                val user = auth.currentUser

                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }

                user?.updateProfile(profileUpdates)?.addOnCompleteListener{ ts ->
                    if (ts.isSuccessful) {
                        val dataUserAuth = UserEntity(user.uid.toString(), name, user.email.toString(), user.photoUrl.toString() ?: "")
                        CoroutineScope(Dispatchers.IO).launch {
                            userDao.insertUser(dataUserAuth)
                        }
                        _userAuth.value = user
                        _messageSuccess.value = "Register Success"
                    }
                }

            } else {
                _messageError.value = "Register Failured"
            }
        }

    }

    fun logOut(id:String) = userDao.logout(id)

    fun signInWithEmail(email: String, password: String) {
        if(email.isEmpty() || password.isEmpty()) {
            _messageError.value = "email dan password is Not Empty"
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                _userAuth.value = auth.currentUser
                _messageSuccess.value = "Login Success"
            } else {
                _messageError.value = "Authentication Failed"
            }
        }
    }

    fun signInWithFacebook(token:AccessToken) {
        val crendetial = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(crendetial).addOnCompleteListener {  task ->
            if (task.isSuccessful) {
                _userAuth.value = auth.currentUser
                _messageSuccess.value = "Login Success"
            } else {
                _messageError.value = "Authentication Failed"
            }
        }
    }

    fun signInWithGoogle(context:Context) {
        val webClientKey:String = context.getString(R.string.web_client_id)
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientKey)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                result.let { handleSigInGoogle(it.credential) }
            } catch (e:Exception) {
                Log.e(TAG, "OnFailure Auth : ${e.message}")
            }
        }
    }

    private fun handleSigInGoogle(credential: Credential) {
        if(credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdCredential = GoogleIdTokenCredential.createFrom(credential.data)

            firebaseAuthWithGoogle(googleIdCredential.idToken)
        } else {
            Log.w(TAG, "Crendetial is not of type Google ID!")
        }
    }


    private fun firebaseAuthWithGoogle(idToken:String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                _userAuth.value = auth.currentUser
                _messageSuccess.value = "Login With Google Success"
            } else {
                Log.w(TAG, "SinginWithCredetial:Failure", task.exception)
            }
        }
    }




    fun signOut(context: Context) {
        auth.signOut()
        val crendetialManager = CredentialManager.create(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                crendetialManager.clearCredentialState(clearRequest)
                _userAuth.value = null
            } catch (e:Exception) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
    }

    fun addStock(name:String,nameSuplier:String, nameBarang:String, codeBarang:String, keterangan:String, jumlah:String, date:String) {
        if (name.isEmpty() || nameSuplier.isEmpty() || nameBarang.isEmpty() || codeBarang.isEmpty() || keterangan.isEmpty() || jumlah.isEmpty()){
            _messageError.value = "nameSuplier, nameBarang, codebarang, keterangan, jumlah tidak boleh kosong"
            return
        }
        val db = Firebase.database
        val ref = db.getReference(name)

        val stock = Stock(name,nameSuplier, nameBarang, codeBarang, keterangan, jumlah, date)
        ref.child("Stock").child(codeBarang).setValue(stock)

        _messageSuccess.value = "Add Data Success "
    }

    fun getStock() {
        _isLoading.value = true
        val dataUsername = auth.currentUser?.displayName
        val database = FirebaseDatabase.getInstance().getReference(dataUsername.toString())
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataStock = mutableListOf<Stock>()

                for (itemSnapshot in snapshot.child("Stock").children) {
                    val stockItem = itemSnapshot.getValue(Stock::class.java)

                    Log.d("ProductViewModel", "Stock Item: $stockItem")

                    if(stockItem != null) {
                        dataStock.add(stockItem)
                    }
                }
                _userStock.value = dataStock
                _isLoading.value =false
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getStockById(id:String) {
        _isLoading.value = true
        val dataUsername = auth.currentUser?.displayName
        val database = FirebaseDatabase.getInstance().getReference(dataUsername.toString())

        database.child("Stock").child(id).get().addOnSuccessListener { snapshot ->
            val stockItem = snapshot.getValue(Stock::class.java)
            Log.d("ProductViewModel", "Stock Item: $stockItem")

            if (stockItem != null) {
                _userStock.value = listOf(stockItem) // simpan sebagai list 1 item
            } else {
                _userStock.value = emptyList()
            }

            _isLoading.value = false
        }.addOnFailureListener {
            Log.e("ProductViewModel", "Gagal ambil data: ${it.message}")
            _isLoading.value = false
        }
    }


    fun updateStockData(
        id: String,
        nameSuplier: String,
        nameBarang: String,
        keterangan: String,
        jumlah: String,
        date: String
    ) {
        _isLoading.value = true

        val dataUsername = auth.currentUser?.displayName

        val database = FirebaseDatabase.getInstance().getReference(dataUsername.toString())
        val stockRef = database.child("Stock").child(id)

        // Buat map data yang akan diupdate
        val updateMap = mapOf<String, Any>(
            "nameSuplier" to nameSuplier,
            "nameBarang" to nameBarang,
            "keterangan" to keterangan,
            "jumlah" to jumlah,
            "date" to date
        )

        stockRef.updateChildren(updateMap)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    // Bisa tambahkan logika sukses (misal tampilkan Toast)
                    Log.d("Update", "Data berhasil diupdate")
                    _messageSuccess.value = "Update Data Success"
                } else {
                    _messageError.value = "Update Data Failed"
                }
            }
    }


    fun deleteStock(id:String) {
        val dataUsername = auth.currentUser?.displayName
        val database = FirebaseDatabase.getInstance().getReference(dataUsername.toString())
        database.child("Stock").child(id).removeValue()
            .addOnSuccessListener {
                getStock() // Refresh data setelah delete sukses
                _isLoading.value = false
                _messageSuccess.value = "delete Data Success"
            }
            .addOnFailureListener {
                _isLoading.value = false
                Log.e("DeleteStock", "Gagal menghapus: ${it.message}")
                _messageError.value = "Error : ${it.message}"
            }
    }

    companion object {
        private val TAG = "Repository"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(
            userDao: UserDao,
            context: Context
        ): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(userDao, context)
            }.also { INSTANCE = it }
    }
}
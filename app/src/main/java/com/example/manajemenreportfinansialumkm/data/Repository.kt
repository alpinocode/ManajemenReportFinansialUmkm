package com.example.manajemenreportfinansialumkm.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.annotation.RequiresApi
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
import com.example.manajemenreportfinansialumkm.helper.Pemasukan
import com.example.manajemenreportfinansialumkm.helper.Pengeluaran
import com.example.manajemenreportfinansialumkm.helper.Stock
import com.example.manajemenreportfinansialumkm.helper.Transaction
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
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random

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

    private val _searchItem = MutableLiveData<List<Stock?>>()
    val searchItem:LiveData<List<Stock?>> = _searchItem

    private val _notification = MutableLiveData<List<Stock>>()
    val notification:LiveData<List<Stock>> = _notification

    private val _userVerification = MutableLiveData<Boolean?>()
    val userVerfication:LiveData<Boolean?> = _userVerification

    private val _userPemasukan = MutableLiveData<String?>()
    val userPemasukan:LiveData<String?> = _userPemasukan

    private val _userPengeluaran = MutableLiveData<String?>()
    val userPengeluaran:LiveData<String?> = _userPengeluaran

    private val _userPengeluaranList = MutableLiveData<List<Pengeluaran>?>()
    val userPengeluaranList:LiveData<List<Pengeluaran>?> = _userPengeluaranList

    private val _userPemasukanList = MutableLiveData<List<Pemasukan>?>()
    val userPemasukanList:LiveData<List<Pemasukan>?> = _userPemasukanList

    private val _userTransaction = MutableLiveData<List<Transaction>>()
    val userTransaction:LiveData<List<Transaction>> = _userTransaction

    private val _userAddTransactionSuccess = MutableLiveData<String?>()
    val userAddTransactionSuccess:LiveData<String?> = _userAddTransactionSuccess

    private val _userAddTransactionError = MutableLiveData<String?>()
    val userAddTransactionError:LiveData<String?> = _userAddTransactionError

    private val _userStockMessageSuccess = MutableLiveData<String?>()
    val userStockMessageSuccess:LiveData<String?> = _userStockMessageSuccess

    private val _userStockMessageError = MutableLiveData<String?>()
    val userStockMessageError:LiveData<String?> = _userStockMessageError

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
                return@addOnCompleteListener
            } else {
                _messageError.value = "Authentication Failed"
                return@addOnCompleteListener
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
                return@addOnCompleteListener
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

    fun userVerification() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{ task ->
            if (task.isSuccessful){
                _messageSuccess.value = "Verification Send Email"
            } else {
                _messageError.value = "Verification Failed"
            }
        }
    }

    fun checkedEmailVerification() {
        val userCheckedVerification = auth.currentUser

        userCheckedVerification?.reload()?.addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                _userVerification.value = userCheckedVerification.isEmailVerified
            } else {
                _userVerification.value = false
            }
        }
    }

    fun addStock( name:String, nameSuplier:String, nameBarang:String, codeBarang:String,  hargaJual:Int, hargaBeli:Int, stock:Int, keterangan:String,  date:String) {
        _userStockMessageError.value = null
        if (name.isEmpty() || nameSuplier.isEmpty() || nameBarang.isEmpty() || codeBarang.isEmpty() || keterangan.isEmpty() || stock.toString().isEmpty() || hargaJual.toString().isEmpty()){
            _userStockMessageError.value = "nameSuplier, nameBarang, codebarang, keterangan, jumlah tidak boleh kosong"
            return
        }
        if(name.length < 3 || nameSuplier.length < 3 || nameBarang.length < 3 || codeBarang.length < 3 ) {
            _userStockMessageError.value = "nameSuplier, nameBarang, codebarang Setidak minimal 3 karakter"
            return
        }
        if(stock <= 0 || hargaBeli <= 0 || hargaJual <= 0) {
            _userStockMessageError.value = "Stock, Harga Beli, Harga Jual tidak boleh 0"
            return
        }
        if(hargaBeli > hargaJual) {
            _userStockMessageError.value = "Harga Beli tidak boleh lebih besar dari Harga Jual"
            return
        }
        _userStockMessageError.value = null
        val db = Firebase.database
        val ref = db.getReference(name)

        val modal = hargaBeli * stock

        val stock = Stock(name,nameSuplier, nameBarang, codeBarang, hargaJual, hargaBeli, modal , stock,keterangan, date)
        ref.child("Stock").child(codeBarang).setValue(stock).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _userStockMessageSuccess.value = "Add Data Stock Success "
            } else {
                _userStockMessageError.value = "Add Data Stock Failed"
            }
        }
        _userStockMessageSuccess.value = null

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


                    val statusStock = stockItem?.status == true
                    if(stockItem != null && statusStock) {
                        stockItem.let { dataStock.add(it) }
                    }
                }
                Log.d("ProductViewModel", "Stock Item: $dataStock")

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
        nameSuplier:String,
        nameBarang:String,
        hargaJual:Int,
        hargaBeli:Int,
        stock:Int,
        keterangan:String,
        modal:Int,
        date:String
    ) {
        _isLoading.value = true

        val dataUsername = auth.currentUser?.displayName

        val database = FirebaseDatabase.getInstance().getReference(dataUsername.toString())
        val stockRef = database.child("Stock").child(id)

        val updateMap = mapOf<String, Any>(
            "nameSuplier" to nameSuplier,
            "nameBarang" to nameBarang,
            "keterangan" to keterangan,
            "stock" to stock,
            "hargaJual" to hargaJual,
            "hargaBeli" to hargaBeli,
            "modal" to modal,
            "date" to date
        )

        stockRef.updateChildren(updateMap)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _userStockMessageSuccess.value = "Update Data Success"
                } else {
                    _messageError.value = "Update Data Failed"
                }
            }
    }

    fun deleteStock(id:String) {
        val dataUsername = auth.currentUser?.displayName
        val database = FirebaseDatabase.getInstance().getReference(dataUsername.toString())
        database.child("Stock").child(id).updateChildren(mapOf("status" to false))
            .addOnSuccessListener {
                getStock()
                _isLoading.value = false
                _userStockMessageSuccess.value = "delete Data Stock Success"
            }
            .addOnFailureListener {
                _isLoading.value = false
                Log.e("DeleteStock", "Gagal menghapus: ${it.message}")
                _messageError.value = "Error : ${it.message}"
            }
    }

    fun getStockMenipis() {
        _isLoading.value = true
        val username = auth.currentUser?.displayName.toString()

        val database = FirebaseDatabase.getInstance().getReference(username).child("Stock").orderByChild("stock").endAt(5.0)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notificationItem = mutableListOf<Stock>()
                for (produkSnapshot in snapshot.children) {
                    val produk = produkSnapshot.getValue(Stock::class.java)
                    produk?.let {
                        if(produk.status == true) {
                            notificationItem.add(it)
                        }
                    }
                }
                Log.d(TAG, "Cek Datanya Notification : ${notificationItem}")
                _notification.value = notificationItem
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Cek Error STock menipis ${error.message}")
            }

        })

    }

    fun searchItem(query: String) {
        val username = auth.currentUser?.displayName.toString()
        val database = FirebaseDatabase.getInstance().getReference(username).child("Stock")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _isLoading.value = true
                val filteredList = mutableListOf<Stock>()
                for (produkSnapshot in snapshot.children) {
                    val produk = produkSnapshot.getValue(Stock::class.java)
                    if (produk != null && produk.status == true && produk.nameBarang!!.contains(query, ignoreCase = true)) {
                        filteredList.add(produk)
                    }
                }
                _searchItem.value = filteredList
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }

    fun updateStockAfterTransaction(id: String, stock: Int) {
        val dataUsername = auth.currentUser?.displayName.toString()
        val refenrence = FirebaseDatabase.getInstance().getReference(dataUsername).child("Stock").child(id)
        refenrence.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(Stock::class.java)
                val updateDataStock = data?.stock.toString().toInt() - stock.toString().toInt()

                val dataUpdate = mapOf<String, Any>(
                    "stock" to updateDataStock
                )

                val database = FirebaseDatabase.getInstance().getReference(dataUsername)
                val stockRefUpdate = database.child("Stock").child(id)

                stockRefUpdate.updateChildren(dataUpdate)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPengeluaran(name:String, codeBarang: String, hargaBeli: Int, stock: Int) {
        val db = Firebase.database
        val ref = db.getReference(name)
        val dateMontNow = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM", Locale("id", "ID")))
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale("id", "ID")))

        val modal = hargaBeli * stock
        val pengeluaran = Pengeluaran(codeBarang, modal, date = date)
        ref.child("Pembukuan").child("Pengeluaran").child(dateMontNow).child(codeBarang).setValue(pengeluaran)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPengeluaranById(id: String, date: String) {
        _isLoading.value = true
        val dataUsername = auth.currentUser?.displayName

        val database = FirebaseDatabase.getInstance()
            .getReference(dataUsername.toString())
            .child("Pembukuan")
            .child("Pengeluaran")
            .child(date)
            .child(id)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataPengeluaran = mutableListOf<Pengeluaran>()
                val pengeluaran = snapshot.getValue(Pengeluaran::class.java)
                Log.d(TAG, "Cek Data Pengeluaran : $pengeluaran")

                if (pengeluaran != null) {
                    dataPengeluaran.add(pengeluaran)
                }
                _userPengeluaranList.value = dataPengeluaran
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database Error: ${error.message}")
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePengeluaran(codeBarang: String, modalBaru: Int) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("UpdatePengeluaran", "User belum login")
            return
        }

        val username = currentUser.displayName ?: "UnknownUser"
        val databaseRef = FirebaseDatabase.getInstance().getReference(username)
            .child("Pembukuan")
            .child("Pengeluaran")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isFound = false

                snapshot.children.forEach { tanggalSnapshot ->
                    if (tanggalSnapshot.hasChild(codeBarang)) {
                        val barangRef = tanggalSnapshot.child(codeBarang).ref

                        val updates = mapOf(
                            "codeBarang" to codeBarang,
                            "totalPengeluaran" to modalBaru
                        )

                        barangRef.updateChildren(updates)
                            .addOnSuccessListener {
                                Log.d("UpdatePengeluaran", "Berhasil update $codeBarang")
                                _userStockMessageSuccess.value = ""
                            }
                            .addOnFailureListener {
                                Log.e("UpdatePengeluaran", "Gagal update: ${it.message}")
                            }

                        isFound = true
                        return@forEach
                    }
                }

                if (!isFound) {
                    Log.d("UpdatePengeluaran", "Data tidak ditemukan untuk codeBarang: $codeBarang")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UpdatePengeluaran", "Gagal akses Firebase: ${error.message}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addPemasukanTransaction(codeBarang: String, harga: Int, stock: Int) {
        val database = Firebase.database
        val dataUsername = auth.currentUser?.displayName.toString()
        val pemasukanRef = database.getReference(dataUsername)
        val dateMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM", Locale("id", "ID")))
        val dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale("id", "ID")))
        val randomId = Random.nextInt(0, 40000)

        val pemasukanData = Pemasukan(codeBarang, harga * stock, dateNow)

        pemasukanRef.child("Pembukuan").child("Pemasukan").child(dateMonth).child(randomId.toString()).setValue(pemasukanData).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                _messageSuccess.value = "Add Pemasukan Success"
            } else {
                _messageError.value = "Add Pemasukan Failed"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPemasukan() {
        _isLoading.value = true

        val dataUsername = auth.currentUser?.displayName.toString()
        val database = FirebaseDatabase.getInstance().getReference(dataUsername)
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val pemasukanRef = database.child("Pembukuan").child("Pemasukan").child(date).orderByChild("totalPemasukan")

        pemasukanRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalPemasukanSemua = 0
                var pemasukanDataList = mutableListOf<Pemasukan>()
                for (pemasukanSnapshot in snapshot.children) {
                    val pemasukan = pemasukanSnapshot.getValue(Pemasukan::class.java)

                    pemasukan?.let {
                        totalPemasukanSemua += it.totalPemasukan.toString().toInt()
                        pemasukanDataList.add(it)
                    }

                }
                _userPemasukan.value = totalPemasukanSemua.toString()
                _userPemasukanList.value = pemasukanDataList
                _isLoading.value = false
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPengeluaran() {
        _isLoading.value = true
        val dataUsername = auth.currentUser?.displayName.toString()
        val database = FirebaseDatabase.getInstance().getReference(dataUsername)
        val dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM", Locale("id", "ID")))
        val pengeluaranRef = database.child("Pembukuan").child("Pengeluaran").child(dateNow).orderByChild("totalPengeluaran")

        pengeluaranRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var pengeluaran = 0
                val pengeluaranDataList = mutableListOf<Pengeluaran>()
                for (pengeluaranSnapshot in snapshot.children) {
                    val dataPengeluaranFromDb = pengeluaranSnapshot.getValue(Pengeluaran::class.java)

                    dataPengeluaranFromDb.let {
                        pengeluaran += it?.totalPengeluaran.toString().toInt()
                        it?.let { it1 -> pengeluaranDataList.add(it1) }
                    }

                }
                _userPengeluaran.value = pengeluaran.toString()
                _userPengeluaranList.value = pengeluaranDataList
                _isLoading.value = false

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun addTransaction(id: String, date: String, namaBarang:String,  harga: Int, quantity:Int) {
        val username = auth.currentUser?.displayName.toString()
        val database = FirebaseDatabase.getInstance().getReference(username)
        val random = Random.nextInt(0, 200000)
        val trasanctionRef = database.child("Order").child(id).child(random.toString())


        val dataTrasanction = Transaction(id, date, namaBarang, quantity, harga * quantity)

        trasanctionRef.setValue(dataTrasanction).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                _userAddTransactionSuccess.value = "Add Transaction Success"
            } else {
                _userAddTransactionError.value = "Add Transaction Failed"
            }
        }
    }

    fun getTransaction() {
        _isLoading.value = true
        val username = auth.currentUser?.displayName.toString()
        val database = FirebaseDatabase.getInstance().getReference(username)
        val getTransactionRef = database.child("Order").orderByChild("namaBarang")

        getTransactionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val trasanctionItem = mutableListOf<Transaction>()

                for (transactionSnapshot in snapshot.children) {
                    for (transactionItem in transactionSnapshot.children) {
                        val transaction = transactionItem.getValue(Transaction::class.java)
                        transaction?.let {
                            trasanctionItem.add(it)
                        }
                    }
                }
                _userTransaction.value = trasanctionItem
                Log.d(TAG, "Cek Data Transaction : ${trasanctionItem}")
                _isLoading.value = false
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
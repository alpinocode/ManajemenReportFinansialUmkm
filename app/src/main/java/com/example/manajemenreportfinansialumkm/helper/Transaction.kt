package com.example.manajemenreportfinansialumkm.helper

data class Transaction(
    val id: String? = "",
    val date:String? = "",
    val namaBarang:String? = "",
    val quantity:Int? = 0,
    val totalHarga:Int? = 0
)
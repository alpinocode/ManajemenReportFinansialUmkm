package com.example.manajemenreportfinansialumkm.helper

data class Pengeluaran(
    val codeBarang:String? = "",
    val totalPengeluaran:Int? = 0
)

data class Pemasukan(
    val codeBarang: String? = "",
    val totalPemasukan:Int? = 0,
)
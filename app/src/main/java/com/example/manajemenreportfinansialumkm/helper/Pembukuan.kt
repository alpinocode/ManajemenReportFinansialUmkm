package com.example.manajemenreportfinansialumkm.helper

data class Pengeluaran(
    val codeBarang:String? = "",
    val totalPengeluaran:Int? = 0,
    val date:String? = ""
)

data class Pemasukan(
    val codeBarang: String? = "",
    val totalPemasukan:Int? = 0,
    val date: String? = ""
)
sealed class PembukuanItem {
    data class PemasukanItem(val data: Pemasukan) : PembukuanItem()
    data class PengeluaranItem(val data: Pengeluaran) : PembukuanItem()
}
package com.example.manajemenreportfinansialumkm.helper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stock(
    val name: String? = null,
    val nameSuplier: String? = null,
    val nameBarang: String? = null,
    val codeBarang: String? = null,
    val kategory:String? = null,
    val harga:Int? = null,
    val stock: Int? = null,
    val keterangan: String? = null,
    val date: String? = null
): Parcelable


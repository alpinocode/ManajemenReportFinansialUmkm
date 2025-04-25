package com.example.manajemenreportfinansialumkm.helper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stock(
    val name: String? = null,
    val nameSuplier: String? = null,
    val nameBarang: String? = null,
    val codeBarang: String? = null,
    val hargaJual:Int? = null,
    val hargaBeli:Int? = null,
    val modal:Int? = null,
    val stock: Int? = null,
    val keterangan: String? = null,
    val date: String? = null,
    val status:Boolean = true
): Parcelable


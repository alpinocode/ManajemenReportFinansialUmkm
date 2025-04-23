package com.example.manajemenreportfinansialumkm.helper

import android.icu.text.DateFormat
import android.icu.text.NumberFormat
import java.util.Locale

fun currencyToRupiah(number:Double):String {
    val localeId = Locale("in", "ID")
    return NumberFormat.getCurrencyInstance(localeId).format(number).replace(",00", "")
}


package com.example.manajemenreportfinansialumkm.helper

import java.util.UUID

data class HelperFirebaseDatabase(
    val id: UUID,
    val name:String,
    val email:String,
    val password:String
)
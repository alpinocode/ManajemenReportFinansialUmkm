package com.example.manajemenreportfinansialumkm.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @ColumnInfo("id")
    @PrimaryKey
    val id:String,

    @ColumnInfo("name")
    val name:String,

    @ColumnInfo("email")
    val email:String,

    @ColumnInfo("photo")
    val urlPhoto:String? = null
)
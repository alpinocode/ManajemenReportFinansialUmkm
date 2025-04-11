package com.example.manajemenreportfinansialumkm.di

import android.content.Context
import com.example.manajemenreportfinansialumkm.data.Repository
import com.example.manajemenreportfinansialumkm.data.local.room.DatabaseRoom

object Injection {
    fun provideRepository(context: Context):Repository {
        val database = DatabaseRoom.getInstance(context)
        val userDao = database.userDao()

        return Repository.getInstance(userDao, context)
    }
}
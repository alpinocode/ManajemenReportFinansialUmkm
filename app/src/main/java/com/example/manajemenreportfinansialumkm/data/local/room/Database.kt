package com.example.manajemenreportfinansialumkm.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.manajemenreportfinansialumkm.data.local.entity.UserEntity


@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class DatabaseRoom : RoomDatabase(){
    abstract fun userDao():UserDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseRoom?= null

        fun getInstance(context:Context):DatabaseRoom =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseRoom::class.java,
                    "ManajemenReportFinansialUmkm.db"
                ).build()
            }

    }
}
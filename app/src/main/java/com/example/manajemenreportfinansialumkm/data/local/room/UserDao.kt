package com.example.manajemenreportfinansialumkm.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.manajemenreportfinansialumkm.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = :id")
    fun getUser(id:String):LiveData<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user:UserEntity)

    @Query("DELETE FROM user WHERE id = :id")
    fun logout(id:String)
}
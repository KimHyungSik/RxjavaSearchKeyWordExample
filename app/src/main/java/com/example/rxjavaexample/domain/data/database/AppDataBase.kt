package com.example.rxjavaexample.domain.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rxjavaexample.domain.data.dao.SearchItemDao
import com.example.rxjavaexample.domain.model.History

@Database(entities = arrayOf(History::class), version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun historyDao(): SearchItemDao
}
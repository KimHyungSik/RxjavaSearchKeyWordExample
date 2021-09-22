package com.example.rxjavaexample.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey(autoGenerate = true) val id : Int?,
    @ColumnInfo(name = "search_item") val item : String,
)
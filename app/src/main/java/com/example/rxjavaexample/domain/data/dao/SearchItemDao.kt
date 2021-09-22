package com.example.rxjavaexample.domain.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rxjavaexample.domain.model.History
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable


@Dao
interface SearchItemDao {

    @Query("select * from History")
    fun findAll() : Flowable<List<History>>

    // Flowable 함수는 서버로 부터 데이터를 가져오는 속도와 처리하는 속도의 차이를 조절하며 작업을 처리 한다
    @Query("Select * from History where search_item LIKE :find || '%'")
    fun findByKeyWord(find: String): Flowable<List<History>>

    // Completable 함수는 데이터를 발생 시키지 않고 작업을 수행만 하고 종료 시킨다
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItem(searchItem : History) : Completable

    @Query("Delete from History")
    fun deleteAll() : Completable
}
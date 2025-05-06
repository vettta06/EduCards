package com.example.educards.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao{
    @Insert
    suspend fun insert(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<Card>)
    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Query("SELECT * FROM cards ORDER BY id DESC")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * FROM cards")
    suspend fun getAllCardsSync(): List<Card>
}
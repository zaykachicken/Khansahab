package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DealDao {
    @Query("SELECT * FROM deals_and_discounts ORDER BY minOrder ASC")
    fun getAllDeals(): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals_and_discounts WHERE isActive = 1 ORDER BY minOrder ASC")
    fun getActiveDeals(): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals_and_discounts WHERE code = :code LIMIT 1")
    suspend fun getDealByCode(code: String): DealEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeal(deal: DealEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDeals(deals: List<DealEntity>)

    @Update
    suspend fun updateDeal(deal: DealEntity)

    @Query("UPDATE deals_and_discounts SET isActive = :isActive WHERE code = :code")
    suspend fun updateDealStatus(code: String, isActive: Boolean)

    @Query("DELETE FROM deals_and_discounts WHERE code = :code")
    suspend fun deleteDeal(code: String)
}

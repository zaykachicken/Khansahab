package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RestaurantContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM restaurant_contact_info WHERE id = 1 LIMIT 1")
    fun getContactInfo(): Flow<RestaurantContactEntity?>

    @Query("SELECT * FROM restaurant_contact_info WHERE id = 1 LIMIT 1")
    suspend fun getContactInfoDirect(): RestaurantContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateContactInfo(info: RestaurantContactEntity)
}

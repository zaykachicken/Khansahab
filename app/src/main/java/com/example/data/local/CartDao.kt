package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY cartId ASC")
    fun getCartItems(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items ORDER BY cartId ASC")
    suspend fun getCartItemsDirect(): List<CartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem): Long

    @Query("UPDATE cart_items SET quantity = :quantity WHERE cartId = :cartId")
    suspend fun updateQuantity(cartId: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE cartId = :cartId")
    suspend fun deleteCartItem(cartId: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

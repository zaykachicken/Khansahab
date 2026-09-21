package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CartItem
import com.example.data.model.FoodItem
import com.example.data.model.OrderEntity
import com.example.data.model.SavedAddress

@Database(
    entities = [
        FoodItem::class,
        CartItem::class,
        OrderEntity::class,
        SavedAddress::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZaykaDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var INSTANCE: ZaykaDatabase? = null

        fun getDatabase(context: Context): ZaykaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZaykaDatabase::class.java,
                    "zayka_food_delivery_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

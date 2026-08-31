package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SaleOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleOrderDao {
    @Query("SELECT * FROM sales_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<SaleOrderEntity>>

    @Query("SELECT * FROM sales_orders WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getOrdersBetween(startTime: Long, endTime: Long): Flow<List<SaleOrderEntity>>

    @Query("SELECT * FROM sales_orders WHERE id = :id")
    suspend fun getOrderById(id: Long): SaleOrderEntity?

    @Query("SELECT MAX(orderNumber) FROM sales_orders")
    suspend fun getMaxOrderNumber(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: SaleOrderEntity): Long

    @Query("DELETE FROM sales_orders WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM sales_orders")
    suspend fun deleteAll()
}

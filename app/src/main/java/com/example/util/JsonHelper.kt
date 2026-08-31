package com.example.util

import com.example.data.model.OrderItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsonHelper {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val orderItemListType = Types.newParameterizedType(List::class.java, OrderItem::class.java)
    private val orderListAdapter = moshi.adapter<List<OrderItem>>(orderItemListType)

    fun orderItemsToJson(items: List<OrderItem>): String {
        return try {
            orderListAdapter.toJson(items)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun jsonToOrderItems(json: String): List<OrderItem> {
        return try {
            orderListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

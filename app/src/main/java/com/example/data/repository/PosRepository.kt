package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.AppDatabase
import com.example.data.db.DefaultMenuData
import com.example.data.model.BusinessProfile
import com.example.data.model.FoodCategory
import com.example.data.model.MenuItemEntity
import com.example.data.model.SaleOrderEntity
import com.example.util.CurrencyFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PosRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val menuItemDao = database.menuItemDao()
    private val saleOrderDao = database.saleOrderDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("caja_rapida_prefs", Context.MODE_PRIVATE)

    private val _businessProfile = MutableStateFlow(loadBusinessProfile())
    val businessProfile = _businessProfile.asStateFlow()

    val allMenuItems: Flow<List<MenuItemEntity>> = menuItemDao.getAllMenuItems()
    val allOrders: Flow<List<SaleOrderEntity>> = saleOrderDao.getAllOrders()

    suspend fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>> {
        return if (category == FoodCategory.TODOS.id) {
            menuItemDao.getAllMenuItems()
        } else {
            menuItemDao.getMenuItemsByCategory(category)
        }
    }

    suspend fun insertMenuItem(item: MenuItemEntity): Long = withContext(Dispatchers.IO) {
        menuItemDao.insert(item)
    }

    suspend fun updateMenuItem(item: MenuItemEntity) = withContext(Dispatchers.IO) {
        menuItemDao.update(item)
    }

    suspend fun deleteMenuItem(item: MenuItemEntity) = withContext(Dispatchers.IO) {
        menuItemDao.delete(item)
    }

    suspend fun resetDefaultMenu() = withContext(Dispatchers.IO) {
        menuItemDao.insertAll(DefaultMenuData.getInitialMenu())
    }

    suspend fun saveOrder(order: SaleOrderEntity): Long = withContext(Dispatchers.IO) {
        val id = saleOrderDao.insert(order)
        incrementNextOrderNumber()
        id
    }

    suspend fun getNextOrderNumber(): Int = withContext(Dispatchers.IO) {
        val maxInDb = saleOrderDao.getMaxOrderNumber() ?: 0
        val savedInPrefs = prefs.getInt("next_order_number", 1)
        maxOf(maxInDb + 1, savedInPrefs)
    }

    private fun incrementNextOrderNumber() {
        val current = prefs.getInt("next_order_number", 1)
        prefs.edit().putInt("next_order_number", current + 1).apply()
    }

    suspend fun deleteOrderById(id: Long) = withContext(Dispatchers.IO) {
        saleOrderDao.deleteById(id)
    }

    suspend fun clearAllOrders() = withContext(Dispatchers.IO) {
        saleOrderDao.deleteAll()
        prefs.edit().putInt("next_order_number", 1).apply()
    }

    private fun loadBusinessProfile(): BusinessProfile {
        val profile = BusinessProfile(
            businessName = prefs.getString("biz_name", "El Rey del Sabor Fast Food") ?: "El Rey del Sabor Fast Food",
            taxId = prefs.getString("biz_tax_id", "NIT: 900.852.147-3") ?: "NIT: 900.852.147-3",
            phone = prefs.getString("biz_phone", "+57 310 555 0199") ?: "+57 310 555 0199",
            address = prefs.getString("biz_address", "Av. Principal # 45-20, Zona Gourmet") ?: "Av. Principal # 45-20, Zona Gourmet",
            currencySymbol = prefs.getString("biz_currency", "$") ?: "$",
            taxRatePercent = prefs.getFloat("biz_tax_rate", 0.0f).toDouble(),
            receiptHeader = prefs.getString("biz_header", "¡Especialistas en Perros Calientes & Burgers!") ?: "¡Especialistas en Perros Calientes & Burgers!",
            receiptFooter = prefs.getString("biz_footer", "¡Gracias por su compra! Vuelva pronto.") ?: "¡Gracias por su compra! Vuelva pronto.",
            cashierName = prefs.getString("biz_cashier", "Cajero 1") ?: "Cajero 1",
            useThousandsSeparator = prefs.getBoolean("biz_use_thousands_sep", true),
            thousandsSeparator = prefs.getString("biz_thousands_sep", ".") ?: "."
        )
        CurrencyFormatter.syncWithProfile(profile)
        return profile
    }

    fun updateBusinessProfile(profile: BusinessProfile) {
        prefs.edit()
            .putString("biz_name", profile.businessName)
            .putString("biz_tax_id", profile.taxId)
            .putString("biz_phone", profile.phone)
            .putString("biz_address", profile.address)
            .putString("biz_currency", profile.currencySymbol)
            .putFloat("biz_tax_rate", profile.taxRatePercent.toFloat())
            .putString("biz_header", profile.receiptHeader)
            .putString("biz_footer", profile.receiptFooter)
            .putString("biz_cashier", profile.cashierName)
            .putBoolean("biz_use_thousands_sep", profile.useThousandsSeparator)
            .putString("biz_thousands_sep", profile.thousandsSeparator)
            .apply()
        CurrencyFormatter.syncWithProfile(profile)
        _businessProfile.value = profile
    }
}

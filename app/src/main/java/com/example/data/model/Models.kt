package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class FoodCategory(val id: String, val displayName: String, val emoji: String) {
    TODOS("TODOS", "Todos", "🍽️"),
    PERROS("PERROS", "Perros Calientes", "🌭"),
    HAMBURGUESAS("HAMBURGUESAS", "Hamburguesas", "🍔"),
    BEBIDAS("BEBIDAS", "Bebidas", "🥤"),
    COMBOS_EXTRAS("COMBOS_EXTRAS", "Combos y Extras", "🍟")
}

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // PERROS, HAMBURGUESAS, BEBIDAS, COMBOS_EXTRAS
    val price: Double,
    val description: String = "",
    val emoji: String = "🌭",
    val isAvailable: Boolean = true,
    val displayOrder: Int = 0
)

@JsonClass(generateAdapter = true)
data class OrderItem(
    val menuItemId: Long,
    val name: String,
    val category: String,
    val unitPrice: Double,
    val quantity: Int,
    val notes: String = "", // e.g. "Sin cebolla", "Doble salsa tártara", "Para llevar"
    val emoji: String = "🌭"
) {
    val subtotal: Double get() = unitPrice * quantity
}

@Entity(tableName = "sales_orders")
data class SaleOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val customerName: String = "Cliente General",
    val orderType: String = "Comer Aquí", // Comer Aquí, Para Llevar, Domicilio
    val tableOrAddress: String = "",
    val itemsJson: String, // Moshi serialized JSON of List<OrderItem>
    val subtotal: Double,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val taxPercent: Double = 0.0,
    val taxAmount: Double = 0.0,
    val tipAmount: Double = 0.0,
    val grandTotal: Double,
    val amountPaid: Double,
    val changeAmount: Double,
    val paymentMethod: String = "Efectivo", // Efectivo, Tarjeta, Transferencia / QR, Nequi / Zelle, Otro
    val cashierName: String = "Caja 1",
    val orderNotes: String = ""
)

data class BusinessProfile(
    val businessName: String = "El Rey del Sabor Fast Food",
    val taxId: String = "NIT: 900.852.147-3",
    val phone: String = "+57 310 555 0199",
    val address: String = "Av. Principal # 45-20, Zona Gourmet",
    val currencySymbol: String = "$",
    val taxRatePercent: Double = 0.0, // Impoconsumo / IVA opcional
    val receiptHeader: String = "¡Especialistas en Perros Calientes & Burgers!",
    val receiptFooter: String = "¡Gracias por su compra! Vuelva pronto.",
    val cashierName: String = "Cajero Principal",
    val useThousandsSeparator: Boolean = true,
    val thousandsSeparator: String = "." // "." (punto) o "," (coma)
)

data class DailySummary(
    val totalRevenue: Double,
    val ordersCount: Int,
    val averageTicket: Double,
    val cashTotal: Double,
    val cardTotal: Double,
    val transferTotal: Double,
    val otherTotal: Double,
    val perrosCount: Int,
    val hamburguesasCount: Int,
    val bebidasCount: Int,
    val combosCount: Int
)

package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.BusinessProfile
import com.example.data.model.DailySummary
import com.example.data.model.FoodCategory
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderItem
import com.example.data.model.SaleOrderEntity
import com.example.data.repository.PosRepository
import com.example.util.JsonHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class PosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PosRepository
    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = PosRepository(db, application)
    }

    val businessProfile: StateFlow<BusinessProfile> = repository.businessProfile

    // Category Filter & Search
    private val _selectedCategory = MutableStateFlow(FoodCategory.TODOS)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // All Menu Items from DB
    val allMenuItems: StateFlow<List<MenuItemEntity>> = repository.allMenuItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Menu Items
    val filteredMenuItems: StateFlow<List<MenuItemEntity>> = combine(
        allMenuItems,
        _selectedCategory,
        _searchQuery
    ) { items, category, query ->
        items.filter { item ->
            val matchCategory = category == FoodCategory.TODOS || item.category == category.id
            val matchQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current Cart
    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    // Order Info
    private val _customerName = MutableStateFlow("Cliente General")
    val customerName = _customerName.asStateFlow()

    private val _orderType = MutableStateFlow("Comer Aquí") // Comer Aquí, Para Llevar, Domicilio
    val orderType = _orderType.asStateFlow()

    private val _tableOrAddress = MutableStateFlow("")
    val tableOrAddress = _tableOrAddress.asStateFlow()

    private val _orderNotes = MutableStateFlow("")
    val orderNotes = _orderNotes.asStateFlow()

    private val _paymentMethod = MutableStateFlow("Efectivo") // Efectivo, Tarjeta, Transferencia / QR, Nequi / Zelle, Otro
    val paymentMethod = _paymentMethod.asStateFlow()

    private val _discountPercent = MutableStateFlow(0.0)
    val discountPercent = _discountPercent.asStateFlow()

    private val _tipAmount = MutableStateFlow(0.0)
    val tipAmount = _tipAmount.asStateFlow()

    private val _amountPaid = MutableStateFlow(0.0)
    val amountPaid = _amountPaid.asStateFlow()

    // Financial Calculations
    val subtotal: StateFlow<Double> = _cartItems.combine(_cartItems) { items, _ ->
        items.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val discountAmount: StateFlow<Double> = combine(subtotal, _discountPercent) { sub, discPercent ->
        (sub * discPercent) / 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val taxAmount: StateFlow<Double> = combine(subtotal, discountAmount, businessProfile) { sub, disc, profile ->
        val taxable = maxOf(0.0, sub - disc)
        (taxable * profile.taxRatePercent) / 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val grandTotal: StateFlow<Double> = combine(subtotal, discountAmount, taxAmount, _tipAmount) { sub, disc, tax, tip ->
        maxOf(0.0, (sub - disc) + tax + tip)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val changeAmount: StateFlow<Double> = combine(_amountPaid, grandTotal, _paymentMethod) { paid, total, method ->
        if (method == "Efectivo") {
            maxOf(0.0, paid - total)
        } else {
            0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Sales Orders History
    val allOrders: StateFlow<List<SaleOrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily Summary / Cierre de caja
    val dailySummary: StateFlow<DailySummary> = allOrders.combine(allOrders) { orders, _ ->
        calculateDailySummary(orders)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailySummary(0.0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0, 0))

    // Active Receipt Dialog State (for current or past order)
    data class ReceiptDisplayState(
        val isShowing: Boolean = false,
        val order: SaleOrderEntity? = null,
        val items: List<OrderItem> = emptyList()
    )

    private val _receiptState = MutableStateFlow(ReceiptDisplayState())
    val receiptState = _receiptState.asStateFlow()

    // UI Feedback / Alerts
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    fun clearToast() {
        _toastMessage.value = null
    }

    fun selectCategory(category: FoodCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addToCart(item: MenuItemEntity) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.menuItemId == item.id && it.notes.isEmpty() }
        if (index != -1) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(
                OrderItem(
                    menuItemId = item.id,
                    name = item.name,
                    category = item.category,
                    unitPrice = item.price,
                    quantity = 1,
                    emoji = item.emoji
                )
            )
        }
        _cartItems.value = current
    }

    fun addCustomItemToCart(name: String, price: Double, category: String, notes: String, emoji: String = "⚡") {
        if (price <= 0.0 || name.isBlank()) return
        val current = _cartItems.value.toMutableList()
        current.add(
            OrderItem(
                menuItemId = -System.currentTimeMillis(), // Negative unique ID for ad-hoc custom items
                name = name.trim(),
                category = category,
                unitPrice = price,
                quantity = 1,
                notes = notes.trim(),
                emoji = emoji
            )
        )
        _cartItems.value = current
    }

    fun incrementCartItem(index: Int) {
        if (index in _cartItems.value.indices) {
            val current = _cartItems.value.toMutableList()
            val item = current[index]
            current[index] = item.copy(quantity = item.quantity + 1)
            _cartItems.value = current
        }
    }

    fun decrementCartItem(index: Int) {
        if (index in _cartItems.value.indices) {
            val current = _cartItems.value.toMutableList()
            val item = current[index]
            if (item.quantity > 1) {
                current[index] = item.copy(quantity = item.quantity - 1)
            } else {
                current.removeAt(index)
            }
            _cartItems.value = current
        }
    }

    fun removeCartItem(index: Int) {
        if (index in _cartItems.value.indices) {
            val current = _cartItems.value.toMutableList()
            current.removeAt(index)
            _cartItems.value = current
        }
    }

    fun updateCartItemNotes(index: Int, notes: String) {
        if (index in _cartItems.value.indices) {
            val current = _cartItems.value.toMutableList()
            current[index] = current[index].copy(notes = notes.trim())
            _cartItems.value = current
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _customerName.value = "Cliente General"
        _orderType.value = "Comer Aquí"
        _tableOrAddress.value = ""
        _orderNotes.value = ""
        _discountPercent.value = 0.0
        _tipAmount.value = 0.0
        _amountPaid.value = 0.0
        _paymentMethod.value = "Efectivo"
    }

    fun setCustomerName(name: String) {
        _customerName.value = name
    }

    fun setOrderType(type: String) {
        _orderType.value = type
    }

    fun setTableOrAddress(value: String) {
        _tableOrAddress.value = value
    }

    fun setOrderNotes(notes: String) {
        _orderNotes.value = notes
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
        if (method != "Efectivo") {
            _amountPaid.value = grandTotal.value
        }
    }

    fun setDiscountPercent(percent: Double) {
        _discountPercent.value = percent
    }

    fun setTipAmount(tip: Double) {
        _tipAmount.value = tip
    }

    fun setAmountPaid(paid: Double) {
        _amountPaid.value = paid
    }

    fun processAndFinalizeSale() {
        if (_cartItems.value.isEmpty()) return

        val method = paymentMethod.value
        val total = grandTotal.value
        if (method == "Efectivo" && _amountPaid.value > 0.0 && _amountPaid.value < total) {
            val missing = total - _amountPaid.value
            val sym = businessProfile.value.currencySymbol
            _toastMessage.value = "Error: El dinero recibido no puede ser menor al total a cobrar. Faltan ${com.example.util.CurrencyFormatter.format(missing, sym)}"
            return
        }

        viewModelScope.launch {
            val nextNumber = repository.getNextOrderNumber()
            val profile = businessProfile.value
            val sub = subtotal.value
            val disc = discountAmount.value
            val tax = taxAmount.value
            val tip = tipAmount.value
            val paid = if (method == "Efectivo") {
                if (_amountPaid.value > 0.0) _amountPaid.value else total
            } else {
                total
            }
            val change = if (method == "Efectivo") maxOf(0.0, paid - total) else 0.0

            val order = SaleOrderEntity(
                orderNumber = nextNumber,
                timestamp = System.currentTimeMillis(),
                customerName = _customerName.value.ifBlank { "Cliente General" },
                orderType = _orderType.value,
                tableOrAddress = _tableOrAddress.value,
                itemsJson = JsonHelper.orderItemsToJson(_cartItems.value),
                subtotal = sub,
                discountPercent = _discountPercent.value,
                discountAmount = disc,
                taxPercent = profile.taxRatePercent,
                taxAmount = tax,
                tipAmount = tip,
                grandTotal = total,
                amountPaid = paid,
                changeAmount = change,
                paymentMethod = method,
                cashierName = profile.cashierName,
                orderNotes = _orderNotes.value
            )

            repository.saveOrder(order)
            val currentItems = _cartItems.value

            // Clear current cart and show receipt dialog
            clearCart()
            _receiptState.value = ReceiptDisplayState(
                isShowing = true,
                order = order,
                items = currentItems
            )
            _toastMessage.value = "¡Venta registrada exitosamente! Orden #$nextNumber"
        }
    }

    fun viewOrderReceipt(order: SaleOrderEntity) {
        val items = JsonHelper.jsonToOrderItems(order.itemsJson)
        _receiptState.value = ReceiptDisplayState(
            isShowing = true,
            order = order,
            items = items
        )
    }

    fun dismissReceipt() {
        _receiptState.value = ReceiptDisplayState(isShowing = false, order = null, items = emptyList())
    }

    fun saveMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.insertMenuItem(item)
                _toastMessage.value = "Producto agregado: ${item.name}"
            } else {
                repository.updateMenuItem(item)
                _toastMessage.value = "Producto actualizado: ${item.name}"
            }
        }
    }

    fun deleteMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.deleteMenuItem(item)
            _toastMessage.value = "Producto eliminado"
        }
    }

    fun resetMenuToDefaults() {
        viewModelScope.launch {
            repository.resetDefaultMenu()
            _toastMessage.value = "Menú restaurado a los valores predeterminados"
        }
    }

    fun updateBusinessProfile(profile: BusinessProfile) {
        repository.updateBusinessProfile(profile)
        _toastMessage.value = "Información del negocio guardada"
    }

    fun deleteOrder(id: Long) {
        viewModelScope.launch {
            repository.deleteOrderById(id)
            _toastMessage.value = "Registro de venta eliminado"
        }
    }

    fun clearAllSalesHistory() {
        viewModelScope.launch {
            repository.clearAllOrders()
            _toastMessage.value = "Historial de ventas limpiado"
        }
    }

    private fun calculateDailySummary(orders: List<SaleOrderEntity>): DailySummary {
        val cal = Calendar.getInstance()
        val todayYear = cal.get(Calendar.YEAR)
        val todayDay = cal.get(Calendar.DAY_OF_YEAR)

        val todayOrders = orders.filter { order ->
            val orderCal = Calendar.getInstance().apply { timeInMillis = order.timestamp }
            orderCal.get(Calendar.YEAR) == todayYear && orderCal.get(Calendar.DAY_OF_YEAR) == todayDay
        }

        val totalRevenue = todayOrders.sumOf { it.grandTotal }
        val count = todayOrders.size
        val avg = if (count > 0) totalRevenue / count else 0.0

        var cash = 0.0
        var card = 0.0
        var transfer = 0.0
        var other = 0.0

        var perros = 0
        var burgs = 0
        var bebidas = 0
        var combos = 0

        for (order in todayOrders) {
            when (order.paymentMethod) {
                "Efectivo" -> cash += order.grandTotal
                "Tarjeta" -> card += order.grandTotal
                "Transferencia / QR", "Nequi / Zelle" -> transfer += order.grandTotal
                else -> other += order.grandTotal
            }

            val items = JsonHelper.jsonToOrderItems(order.itemsJson)
            for (it in items) {
                when (it.category) {
                    "PERROS" -> perros += it.quantity
                    "HAMBURGUESAS" -> burgs += it.quantity
                    "BEBIDAS" -> bebidas += it.quantity
                    else -> combos += it.quantity
                }
            }
        }

        return DailySummary(
            totalRevenue = totalRevenue,
            ordersCount = count,
            averageTicket = avg,
            cashTotal = cash,
            cardTotal = card,
            transferTotal = transfer,
            otherTotal = other,
            perrosCount = perros,
            hamburguesasCount = burgs,
            bebidasCount = bebidas,
            combosCount = combos
        )
    }
}

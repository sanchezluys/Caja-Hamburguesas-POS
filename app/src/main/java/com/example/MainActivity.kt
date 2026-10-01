package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.CheckoutSheet
import com.example.ui.screens.MenuManagementScreen
import com.example.ui.screens.PosCashRegisterScreen
import com.example.ui.screens.ReceiptDialog
import com.example.ui.screens.SalesHistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.FastFoodOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PosViewModel
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector, val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector) {
    CAJA("Caja", Icons.Filled.PointOfSale, Icons.Outlined.PointOfSale),
    HISTORIAL("Ventas", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong),
    MENU("Menú", Icons.Filled.Fastfood, Icons.Outlined.Fastfood),
    AJUSTES("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: PosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: PosViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(MainTab.CAJA) }
    var isCheckoutOpen by remember { mutableStateOf(false) }

    // State from ViewModel
    val menuItems by viewModel.filteredMenuItems.collectAsStateWithLifecycle()
    val allMenuItems by viewModel.allMenuItems.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val businessProfile by viewModel.businessProfile.collectAsStateWithLifecycle()

    val subtotal by viewModel.subtotal.collectAsStateWithLifecycle()
    val discountPercent by viewModel.discountPercent.collectAsStateWithLifecycle()
    val discountAmount by viewModel.discountAmount.collectAsStateWithLifecycle()
    val taxPercent = businessProfile.taxRatePercent
    val taxAmount by viewModel.taxAmount.collectAsStateWithLifecycle()
    val tipAmount by viewModel.tipAmount.collectAsStateWithLifecycle()
    val grandTotal by viewModel.grandTotal.collectAsStateWithLifecycle()
    val amountPaid by viewModel.amountPaid.collectAsStateWithLifecycle()
    val changeAmount by viewModel.changeAmount.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()

    val customerName by viewModel.customerName.collectAsStateWithLifecycle()
    val orderType by viewModel.orderType.collectAsStateWithLifecycle()
    val tableOrAddress by viewModel.tableOrAddress.collectAsStateWithLifecycle()
    val orderNotes by viewModel.orderNotes.collectAsStateWithLifecycle()

    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val dailySummary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val receiptState by viewModel.receiptState.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            MainTab.CAJA -> "Caja Rápida POS 🌭🍔"
                            MainTab.HISTORIAL -> "Historial y Cierre de Caja"
                            MainTab.MENU -> "Gestión de Menú"
                            MainTab.AJUSTES -> "Ajustes de Negocio & Recibo"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                MainTab.values().forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FastFoodOrange,
                            selectedTextColor = FastFoodOrange,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.CAJA -> {
                    PosCashRegisterScreen(
                        menuItems = menuItems,
                        cartItems = cartItems,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        subtotal = subtotal,
                        grandTotal = grandTotal,
                        businessProfile = businessProfile,
                        onCategorySelected = { viewModel.selectCategory(it) },
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        onAddToCart = { viewModel.addToCart(it) },
                        onIncrementCartItem = { viewModel.incrementCartItem(it) },
                        onDecrementCartItem = { viewModel.decrementCartItem(it) },
                        onAddCustomItem = { name, price, category, notes ->
                            viewModel.addCustomItemToCart(name, price, category, notes)
                        },
                        onClearCart = { viewModel.clearCart() },
                        onOpenCheckout = { isCheckoutOpen = true }
                    )
                }

                MainTab.HISTORIAL -> {
                    SalesHistoryScreen(
                        orders = allOrders,
                        dailySummary = dailySummary,
                        businessProfile = businessProfile,
                        onViewReceipt = { order ->
                            viewModel.viewOrderReceipt(order)
                        },
                        onDeleteOrder = { viewModel.deleteOrder(it) },
                        onClearAllOrders = { viewModel.clearAllSalesHistory() }
                    )
                }

                MainTab.MENU -> {
                    MenuManagementScreen(
                        menuItems = allMenuItems,
                        businessProfile = businessProfile,
                        onSaveItem = { viewModel.saveMenuItem(it) },
                        onDeleteItem = { viewModel.deleteMenuItem(it) },
                        onResetDefaults = { viewModel.resetMenuToDefaults() }
                    )
                }

                MainTab.AJUSTES -> {
                    SettingsScreen(
                        currentProfile = businessProfile,
                        onSaveProfile = { viewModel.updateBusinessProfile(it) }
                    )
                }
            }
        }
    }

    // Checkout Sheet Modal
    if (isCheckoutOpen && cartItems.isNotEmpty()) {
        CheckoutSheet(
            cartItems = cartItems,
            subtotal = subtotal,
            discountPercent = discountPercent,
            discountAmount = discountAmount,
            taxPercent = taxPercent,
            taxAmount = taxAmount,
            tipAmount = tipAmount,
            grandTotal = grandTotal,
            amountPaid = amountPaid,
            changeAmount = changeAmount,
            paymentMethod = paymentMethod,
            customerName = customerName,
            orderType = orderType,
            tableOrAddress = tableOrAddress,
            orderNotes = orderNotes,
            businessProfile = businessProfile,
            onIncrementItem = { viewModel.incrementCartItem(it) },
            onDecrementItem = { viewModel.decrementCartItem(it) },
            onRemoveItem = { viewModel.removeCartItem(it) },
            onUpdateNotes = { idx, notes -> viewModel.updateCartItemNotes(idx, notes) },
            onCustomerNameChange = { viewModel.setCustomerName(it) },
            onOrderTypeChange = { viewModel.setOrderType(it) },
            onTableOrAddressChange = { viewModel.setTableOrAddress(it) },
            onOrderNotesChange = { viewModel.setOrderNotes(it) },
            onPaymentMethodChange = { viewModel.setPaymentMethod(it) },
            onDiscountChange = { viewModel.setDiscountPercent(it) },
            onTipChange = { viewModel.setTipAmount(it) },
            onAmountPaidChange = { viewModel.setAmountPaid(it) },
            onConfirmSale = {
                viewModel.processAndFinalizeSale()
                isCheckoutOpen = false
            },
            onDismiss = { isCheckoutOpen = false }
        )
    }

    // Receipt Dialog (Triggered after sale or when reprinting past order)
    if (receiptState.isShowing && receiptState.order != null) {
        ReceiptDialog(
            order = receiptState.order!!,
            items = receiptState.items,
            business = businessProfile,
            onDismiss = { viewModel.dismissReceipt() }
        )
    }
}

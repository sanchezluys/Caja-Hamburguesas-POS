package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BusinessProfile
import com.example.data.model.OrderItem
import com.example.ui.theme.FastFoodGreen
import com.example.ui.theme.FastFoodOrange
import com.example.util.CurrencyFormatter
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CheckoutSheet(
    cartItems: List<OrderItem>,
    subtotal: Double,
    discountPercent: Double,
    discountAmount: Double,
    taxPercent: Double,
    taxAmount: Double,
    tipAmount: Double,
    grandTotal: Double,
    amountPaid: Double,
    changeAmount: Double,
    paymentMethod: String,
    customerName: String,
    orderType: String,
    tableOrAddress: String,
    orderNotes: String,
    businessProfile: BusinessProfile,
    onIncrementItem: (Int) -> Unit,
    onDecrementItem: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onUpdateNotes: (Int, String) -> Unit,
    onCustomerNameChange: (String) -> Unit,
    onOrderTypeChange: (String) -> Unit,
    onTableOrAddressChange: (String) -> Unit,
    onOrderNotesChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onDiscountChange: (Double) -> Unit,
    onTipChange: (Double) -> Unit,
    onAmountPaidChange: (Double) -> Unit,
    onConfirmSale: () -> Unit,
    onDismiss: () -> Unit
) {
    val sym = businessProfile.currencySymbol
    var editingNoteIndex by remember { mutableStateOf<Int?>(null) }
    var currentEditingNote by remember { mutableStateOf("") }

    var manualCashInput by remember(amountPaid) {
        mutableStateOf(CurrencyFormatter.formatForInput(amountPaid))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detalle del Pedido & Cobro",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: Order Items
                item {
                    Text(
                        text = "PRODUCTOS SELECCIONADOS (${cartItems.sumOf { it.quantity }})",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                itemsIndexed(cartItems) { index, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${CurrencyFormatter.format(item.unitPrice, sym)} c/u",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Stepper Controls
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onDecrementItem(index) },
                                        modifier = Modifier.size(32.dp).testTag("decrement_cart_item_$index")
                                    ) {
                                        Icon(
                                            imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                                            contentDescription = "Disminuir",
                                            tint = if (item.quantity == 1) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "${item.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    IconButton(
                                        onClick = { onIncrementItem(index) },
                                        modifier = Modifier.size(32.dp).testTag("increment_cart_item_$index")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Aumentar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = CurrencyFormatter.format(item.subtotal, sym),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            // Notes / Customization
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item.notes.isNotBlank()) {
                                    Text(
                                        text = "Nota: ${item.notes}",
                                        fontSize = 12.sp,
                                        color = Color(0xFFEA580C),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }

                                TextButton(
                                    onClick = {
                                        editingNoteIndex = index
                                        currentEditingNote = item.notes
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (item.notes.isBlank()) "+ Nota/Personalizar" else "Editar Nota",
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 2: Order Type & Customer Details
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TIPO DE PEDIDO & CLIENTE",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Comer Aquí", Icons.Default.Restaurant, "Mesa"),
                            Triple("Para Llevar", Icons.Default.ShoppingBag, "Empaque"),
                            Triple("Domicilio", Icons.Default.DeliveryDining, "Entrega")
                        ).forEach { (type, icon, _) ->
                            val isSelected = orderType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { onOrderTypeChange(type) },
                                label = { Text(type, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = onCustomerNameChange,
                            label = { Text("Nombre del Cliente", fontSize = 12.sp) },
                            placeholder = { Text("Ej. Carlos Pérez", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f).testTag("customer_name_input")
                        )
                        OutlinedTextField(
                            value = tableOrAddress,
                            onValueChange = onTableOrAddressChange,
                            label = { Text(if (orderType == "Domicilio") "Dirección / Tel" else "Mesa / Referencia", fontSize = 12.sp) },
                            placeholder = { Text(if (orderType == "Domicilio") "Calle 10 # 5-2" else "Mesa 3", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("table_or_address_input")
                        )
                    }
                }

                // Section 3: Descuento & Propina
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "DESCUENTOS & PROPINA (OPCIONAL)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0.0, 5.0, 10.0, 15.0, 20.0).forEach { disc ->
                            val isSelected = discountPercent == disc
                            FilterChip(
                                selected = isSelected,
                                onClick = { onDiscountChange(disc) },
                                label = { Text(if (disc == 0.0) "Sin Desc." else "-${disc.toInt()}%", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Section 4: Métodos de Pago & Calculadora de Cambio
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "MÉTODO DE PAGO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("Efectivo", Icons.Default.LocalAtm),
                            Pair("Tarjeta", Icons.Default.CreditCard),
                            Pair("Transferencia / QR", Icons.Default.QrCode),
                            Pair("Nequi / Zelle", Icons.Default.Store)
                        ).forEach { (method, icon) ->
                            val isSelected = paymentMethod == method
                            FilterChip(
                                selected = isSelected,
                                onClick = { onPaymentMethodChange(method) },
                                label = { Text(method, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FastFoodOrange,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("payment_chip_$method")
                            )
                        }
                    }
                }

                // Section 5: Dynamic Cash Register Calculator & Denomination Shortcuts (when Cash is active)
                if (paymentMethod == "Efectivo") {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "CALCULADORA DE EFECTIVO & VUELTO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Total: ${CurrencyFormatter.format(grandTotal, sym)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Cash Buttons tailored to current grand total
                                val exactAmount = grandTotal
                                val roundAmounts = calculateSuggestedCashButtons(grandTotal)

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            onAmountPaidChange(exactAmount)
                                            manualCashInput = CurrencyFormatter.formatForInput(exactAmount)
                                        },
                                        modifier = Modifier.testTag("cash_exact_button")
                                    ) {
                                        Text("Exacto (${CurrencyFormatter.format(exactAmount, sym)})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    roundAmounts.forEach { cashVal ->
                                        Button(
                                            onClick = {
                                                onAmountPaidChange(cashVal)
                                                manualCashInput = CurrencyFormatter.formatForInput(cashVal)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.surface,
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(CurrencyFormatter.format(cashVal, sym), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val isCashMethod = paymentMethod == "Efectivo"
                                val isCashInsufficient = isCashMethod && manualCashInput.isNotBlank() && amountPaid > 0.0 && amountPaid < grandTotal

                                // Custom Amount Input
                                OutlinedTextField(
                                    value = manualCashInput,
                                    onValueChange = { input ->
                                        val clean = input.filter { it.isDigit() || it == '.' || it == ',' }
                                        manualCashInput = clean
                                        val amount = CurrencyFormatter.parseInputAmount(clean)
                                        onAmountPaidChange(amount)
                                    },
                                    label = { Text("Monto Recibido del Cliente ($sym)", fontSize = 12.sp) },
                                    placeholder = { Text("Monto exacto: ${CurrencyFormatter.format(grandTotal, sym)}", fontSize = 12.sp) },
                                    isError = isCashInsufficient,
                                    supportingText = {
                                        if (isCashInsufficient) {
                                            Text(
                                                text = "⚠️ El monto recibido (${CurrencyFormatter.format(amountPaid, sym)}) no puede ser menor al total a cobrar (${CurrencyFormatter.format(grandTotal, sym)}). Faltan ${CurrencyFormatter.format(grandTotal - amountPaid, sym)}.",
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else if (manualCashInput.isBlank() || amountPaid == 0.0) {
                                            Text(
                                                text = "Si se deja vacío, se asume pago exacto de ${CurrencyFormatter.format(grandTotal, sym)} (Vueltos: $0)",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        } else {
                                            Text(
                                                text = "Monto cubierto correctamente.",
                                                fontSize = 11.sp,
                                                color = Color(0xFF166534)
                                            )
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("cash_received_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Live Change Display Box
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            isCashInsufficient -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.65f)
                                            amountPaid > grandTotal -> FastFoodGreen.copy(alpha = 0.15f)
                                            else -> Color(0xFFF3ECE6)
                                        }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = when {
                                                    isCashInsufficient -> "⚠️ DINERO RECIBIDO INSUFICIENTE"
                                                    amountPaid > grandTotal -> "CAMBIO / VUELTO A ENTREGAR"
                                                    else -> "PAGO EXACTO (VUELTO: $0)"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    isCashInsufficient -> MaterialTheme.colorScheme.error
                                                    amountPaid > grandTotal -> Color(0xFF166534)
                                                    else -> Color(0xFF4B5563)
                                                }
                                            )
                                            if (isCashInsufficient) {
                                                Text(
                                                    text = "Faltan ${CurrencyFormatter.format(grandTotal - amountPaid, sym)} para cubrir el total",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            } else if (amountPaid > grandTotal) {
                                                Text(
                                                    text = "Entregar al cliente",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF166534)
                                                )
                                            }
                                        }
                                        Text(
                                            text = when {
                                                isCashInsufficient -> "-${CurrencyFormatter.format(grandTotal - amountPaid, sym)}"
                                                amountPaid > grandTotal -> CurrencyFormatter.format(amountPaid - grandTotal, sym)
                                                else -> CurrencyFormatter.format(0.0, sym)
                                            },
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = when {
                                                isCashInsufficient -> MaterialTheme.colorScheme.error
                                                amountPaid > grandTotal -> Color(0xFF166534)
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Financial Summary Bar & Finalize Button
            Divider()
            Spacer(modifier = Modifier.height(10.dp))

            val isCashMethod = paymentMethod == "Efectivo"
            val isCashInsufficient = isCashMethod && manualCashInput.isNotBlank() && amountPaid > 0.0 && amountPaid < grandTotal

            // Subtotal and Grand Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Subtotal: ${CurrencyFormatter.format(subtotal, sym)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (discountAmount > 0) {
                        Text(
                            text = "Descuento (${discountPercent}%): -${CurrencyFormatter.format(discountAmount, sym)}",
                            fontSize = 11.sp,
                            color = Color(0xFFDC2626)
                        )
                    }
                    if (taxAmount > 0) {
                        Text(
                            text = "Impuesto (${taxPercent}%): +${CurrencyFormatter.format(taxAmount, sym)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TOTAL A COBRAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = CurrencyFormatter.format(grandTotal, sym),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Error banner before button if insufficient
            if (isCashInsufficient) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "No se puede cobrar: El dinero recibido es menor al total a cobrar (${CurrencyFormatter.format(grandTotal, sym)}).",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Primary Confirm and Print Button
            Button(
                onClick = onConfirmSale,
                enabled = !isCashInsufficient,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_sale_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FastFoodOrange,
                    disabledContainerColor = Color(0xFFD1D5DB)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCashInsufficient) "Monto Recibido Insuficiente" else "Cobrar e Imprimir Recibo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal dialog to edit line item notes
    if (editingNoteIndex != null) {
        Dialog(onDismissRequest = { editingNoteIndex = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Personalización / Nota",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset tags for fast food
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Sin cebolla", "Sin salsas", "Salsa tártara extra",
                            "Doble queso", "Bien cocido", "Para llevar", "Sin hielo", "Extra tocineta"
                        ).forEach { tag ->
                            OutlinedButton(
                                onClick = {
                                    currentEditingNote = if (currentEditingNote.isBlank()) tag else "$currentEditingNote, $tag"
                                }
                            ) {
                                Text(tag, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = currentEditingNote,
                        onValueChange = { currentEditingNote = it },
                        label = { Text("Nota personalizada") },
                        placeholder = { Text("Ej. Sin mostaza, salsa aparte") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { editingNoteIndex = null }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                editingNoteIndex?.let { idx ->
                                    onUpdateNotes(idx, currentEditingNote)
                                }
                                editingNoteIndex = null
                            }
                        ) {
                            Text("Guardar Nota")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calculates smart round cash denominations above the total
 */
private fun calculateSuggestedCashButtons(total: Double): List<Double> {
    val results = mutableListOf<Double>()
    if (total <= 0.0) return results

    // If total in thousands (e.g., $15.500)
    if (total > 1000) {
        val next2k = ceil(total / 2000.0) * 2000.0
        val next5k = ceil(total / 5000.0) * 5000.0
        val next10k = ceil(total / 10000.0) * 10000.0
        val next20k = ceil(total / 20000.0) * 20000.0
        val next50k = ceil(total / 50000.0) * 50000.0
        val next100k = ceil(total / 100000.0) * 100000.0

        listOf(next2k, next5k, next10k, next20k, next50k, next100k).forEach { cash ->
            if (cash > total && !results.contains(cash)) {
                results.add(cash)
            }
        }
    } else {
        // Smaller currencies (e.g., $15.50)
        val next5 = ceil(total / 5.0) * 5.0
        val next10 = ceil(total / 10.0) * 10.0
        val next20 = ceil(total / 20.0) * 20.0
        val next50 = ceil(total / 50.0) * 50.0
        val next100 = ceil(total / 100.0) * 100.0

        listOf(next5, next10, next20, next50, next100).forEach { cash ->
            if (cash > total && !results.contains(cash)) {
                results.add(cash)
            }
        }
    }

    return results.take(4)
}

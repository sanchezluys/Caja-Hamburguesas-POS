package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.data.model.DailySummary
import com.example.data.model.SaleOrderEntity
import com.example.ui.theme.FastFoodGreen
import com.example.ui.theme.FastFoodMustard
import com.example.ui.theme.FastFoodOrange
import com.example.util.CurrencyFormatter
import com.example.util.JsonHelper
import com.example.util.ReceiptPrinter

@Composable
fun SalesHistoryScreen(
    orders: List<SaleOrderEntity>,
    dailySummary: DailySummary,
    businessProfile: BusinessProfile,
    onViewReceipt: (SaleOrderEntity) -> Unit,
    onDeleteOrder: (Long) -> Unit,
    onClearAllOrders: () -> Unit
) {
    val context = LocalContext.current
    val sym = businessProfile.currencySymbol
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var orderToDelete by remember { mutableStateOf<SaleOrderEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Daily Summary Card (Cierre de Caja)
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PointOfSale,
                                contentDescription = null,
                                tint = FastFoodOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cierre de Caja del Día",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Share Daily Shift Report
                        IconButton(
                            onClick = {
                                shareDailyShiftReport(context, dailySummary, businessProfile)
                            },
                            modifier = Modifier.size(32.dp).testTag("share_daily_report_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir Cierre",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Total & Orders metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp),
                            color = FastFoodOrange.copy(alpha = 0.12f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("TOTAL RECAUDADO HOY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FastFoodOrange)
                                Text(
                                    text = CurrencyFormatter.format(dailySummary.totalRevenue, sym),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("VENTAS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${dailySummary.ordersCount} pedidos",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Prom: ${CurrencyFormatter.format(dailySummary.averageTicket, sym)}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    // Breakdown by Payment Method
                    Text("DESGLOSE POR MÉTODO DE PAGO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryMetricChip("💵 Efectivo", CurrencyFormatter.format(dailySummary.cashTotal, sym))
                        SummaryMetricChip("💳 Tarjeta", CurrencyFormatter.format(dailySummary.cardTotal, sym))
                        SummaryMetricChip("📱 Transf./QR", CurrencyFormatter.format(dailySummary.transferTotal, sym))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fast Food Category Volume Sold Today
                    Text("CANTIDAD VENDIDA POR CATEGORÍA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CategoryCountPill("🌭 Perros", dailySummary.perrosCount)
                        CategoryCountPill("🍔 Burgers", dailySummary.hamburguesasCount)
                        CategoryCountPill("🥤 Bebidas", dailySummary.bebidasCount)
                        CategoryCountPill("🍟 Combos", dailySummary.combosCount)
                    }
                }
            }
        }

        // Section Title: Historical Sales
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTORIAL DE VENTAS (${orders.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (orders.isNotEmpty()) {
                    TextButton(
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Text("Limpiar Historial", fontSize = 12.sp, color = Color(0xFFDC2626))
                    }
                }
            }
        }

        if (orders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No hay ventas registradas aún",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Las ventas que realices en la Caja aparecerán aquí.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(orders, key = { it.id }) { order ->
                val itemsList = JsonHelper.jsonToOrderItems(order.itemsJson)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewReceipt(order) }
                        .testTag("order_history_card_${order.orderNumber}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = FastFoodOrange.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "#${order.orderNumber}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = FastFoodOrange,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = order.customerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Text(
                                text = CurrencyFormatter.format(order.grandTotal, sym),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Timestamp & Order Type
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = CurrencyFormatter.formatDateShort(order.timestamp),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = order.orderType,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = if (order.paymentMethod == "Efectivo") FastFoodGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = order.paymentMethod,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (order.paymentMethod == "Efectivo") Color(0xFF166534) else MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Items summary text
                        val summaryText = itemsList.joinToString(", ") { "${it.quantity}x ${it.name}" }
                        Text(
                            text = summaryText,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(4.dp))

                        // Action Buttons: Print, Share, Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onViewReceipt(order) },
                                modifier = Modifier.testTag("reprint_order_${order.orderNumber}")
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ver / Reimprimir Recibo", fontSize = 12.sp)
                            }

                            IconButton(
                                onClick = {
                                    ReceiptPrinter.shareReceipt(context, order, itemsList, businessProfile)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir", tint = MaterialTheme.colorScheme.primary)
                            }

                            IconButton(
                                onClick = { orderToDelete = order },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete single order dialog
    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = { Text("Eliminar Registro") },
            text = { Text("¿Deseas eliminar el registro de venta #${order.orderNumber} por ${CurrencyFormatter.format(order.grandTotal, sym)}?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteOrder(order.id)
                        orderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Clear all history dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Limpiar Todo el Historial") },
            text = { Text("¿Estás seguro de que deseas borrar todas las ventas registradas y reiniciar el contador de órdenes?") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllOrders()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Borrar Todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SummaryMetricChip(label: String, value: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CategoryCountPill(label: String, count: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(4.dp))
            Text("$count", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = FastFoodOrange)
        }
    }
}

private fun shareDailyShiftReport(context: Context, summary: DailySummary, business: BusinessProfile) {
    val sym = business.currencySymbol
    val report = """
        ================================
        📊 REPORTE DE CIERRE DE CAJA
        ${business.businessName.uppercase()}
        Fecha: ${CurrencyFormatter.formatDate(System.currentTimeMillis())}
        Atendido por: ${business.cashierName}
        ================================
        TOTAL RECAUDADO: ${CurrencyFormatter.format(summary.totalRevenue, sym)}
        Órdenes Realizadas: ${summary.ordersCount}
        Ticket Promedio: ${CurrencyFormatter.format(summary.averageTicket, sym)}
        --------------------------------
        MÉTODOS DE PAGO:
        💵 Efectivo: ${CurrencyFormatter.format(summary.cashTotal, sym)}
        💳 Tarjeta: ${CurrencyFormatter.format(summary.cardTotal, sym)}
        📱 Transf./QR: ${CurrencyFormatter.format(summary.transferTotal, sym)}
        --------------------------------
        PRODUCTOS VENDIDOS:
        🌭 Perros Calientes: ${summary.perrosCount}
        🍔 Hamburguesas: ${summary.hamburguesasCount}
        🥤 Bebidas: ${summary.bebidasCount}
        🍟 Combos & Extras: ${summary.combosCount}
        ================================
        Generado con Caja Rápida POS
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, report)
        putExtra(Intent.EXTRA_SUBJECT, "Cierre de Caja - ${business.businessName}")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Compartir Reporte de Caja")
    context.startActivity(shareIntent)
}

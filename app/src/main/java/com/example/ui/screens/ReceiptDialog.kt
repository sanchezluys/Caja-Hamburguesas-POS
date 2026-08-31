package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BusinessProfile
import com.example.data.model.OrderItem
import com.example.data.model.SaleOrderEntity
import com.example.util.CurrencyFormatter
import com.example.util.ReceiptPrinter

@Composable
fun ReceiptDialog(
    order: SaleOrderEntity,
    items: List<OrderItem>,
    business: BusinessProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sym = business.currencySymbol

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recibo de Venta #${order.orderNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_receipt_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Thermal Receipt Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFDF9))
                        .border(1.dp, Color(0xFFE2D8CE), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Business Info
                        Text(
                            text = business.businessName.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF1E1E1E)
                        )
                        if (business.receiptHeader.isNotBlank()) {
                            Text(
                                text = business.receiptHeader,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF4A4A4A)
                            )
                        }
                        if (business.taxId.isNotBlank()) {
                            Text(
                                text = business.taxId,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF4A4A4A)
                            )
                        }
                        if (business.address.isNotBlank()) {
                            Text(
                                text = business.address,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF4A4A4A)
                            )
                        }
                        if (business.phone.isNotBlank()) {
                            Text(
                                text = "Tel: ${business.phone}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF4A4A4A)
                            )
                        }

                        DashedDivider()

                        // Order Metadata
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ORDEN: #${order.orderNumber}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF1E1E1E)
                            )
                            Text(
                                text = order.orderType.uppercase(),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "FECHA: ${CurrencyFormatter.formatDate(order.timestamp)}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF4A4A4A)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CAJERO: ${order.cashierName}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF4A4A4A)
                            )
                        }
                        if (order.customerName.isNotBlank() && order.customerName != "Cliente General") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "CLIENTE: ${order.customerName}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color(0xFF4A4A4A)
                                )
                            }
                        }
                        if (order.tableOrAddress.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "MESA/DIR: ${order.tableOrAddress}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color(0xFF4A4A4A)
                                )
                            }
                        }

                        DashedDivider()

                        // Products Column Headers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CANT / DESCRIPCIÓN",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF1E1E1E)
                            )
                            Text(
                                text = "TOTAL",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF1E1E1E)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Line items
                        items.forEach { item ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "${item.quantity}x ${item.name}",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E1E1E),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = CurrencyFormatter.format(item.subtotal, sym),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E1E1E)
                                    )
                                }
                                if (item.notes.isNotBlank()) {
                                    Text(
                                        text = "   * ${item.notes}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }

                        DashedDivider()

                        // Subtotal and adjustments
                        ReceiptSummaryRow("Subtotal", CurrencyFormatter.format(order.subtotal, sym))
                        if (order.discountAmount > 0) {
                            ReceiptSummaryRow(
                                "Descuento (${order.discountPercent}%)",
                                "- " + CurrencyFormatter.format(order.discountAmount, sym),
                                color = Color(0xFFDC2626)
                            )
                        }
                        if (order.taxAmount > 0) {
                            ReceiptSummaryRow(
                                "Impuesto (${order.taxPercent}%)",
                                "+ " + CurrencyFormatter.format(order.taxAmount, sym)
                            )
                        }
                        if (order.tipAmount > 0) {
                            ReceiptSummaryRow(
                                "Propina",
                                "+ " + CurrencyFormatter.format(order.tipAmount, sym)
                            )
                        }

                        DoubleDashedDivider()

                        // Grand Total in Large Monospace
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL:",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF1E1E1E)
                            )
                            Text(
                                text = CurrencyFormatter.format(order.grandTotal, sym),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = Color(0xFF1E1E1E)
                            )
                        }

                        DoubleDashedDivider()

                        // Payment Details & Change
                        ReceiptSummaryRow("Método de Pago", order.paymentMethod, isBold = true)
                        if (order.paymentMethod == "Efectivo") {
                            ReceiptSummaryRow("Efectivo Recibido", CurrencyFormatter.format(order.amountPaid, sym))
                            ReceiptSummaryRow(
                                "CAMBIO / VUELTO",
                                CurrencyFormatter.format(order.changeAmount, sym),
                                isBold = true,
                                color = Color(0xFF16A34A)
                            )
                        }

                        if (order.orderNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Notas: ${order.orderNotes}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF4A4A4A),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        DashedDivider()

                        // Footer
                        Text(
                            text = business.receiptFooter,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF4A4A4A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "::: CAJA RÁPIDA POS :::",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Print & Share & New Sale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Print Button (Real Android PrintManager)
                    Button(
                        onClick = {
                            ReceiptPrinter.printReceipt(context, order, items, business)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("print_receipt_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Imprimir", fontWeight = FontWeight.Bold)
                    }

                    // Share Button (WhatsApp / SMS)
                    OutlinedButton(
                        onClick = {
                            ReceiptPrinter.shareReceipt(context, order, items, business)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_receipt_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compartir", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Done / Nueva Venta Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("done_receipt_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Nueva Venta / Listo", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ReceiptSummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: Color = Color(0xFF1E1E1E)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            color = color
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            color = color
        )
    }
}

@Composable
private fun DashedDivider() {
    Text(
        text = "--------------------------------",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = Color(0xFF9E9E9E),
        modifier = Modifier.padding(vertical = 4.dp),
        letterSpacing = 1.sp
    )
}

@Composable
private fun DoubleDashedDivider() {
    Text(
        text = "================================",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = Color(0xFF424242),
        modifier = Modifier.padding(vertical = 4.dp),
        letterSpacing = 1.sp
    )
}

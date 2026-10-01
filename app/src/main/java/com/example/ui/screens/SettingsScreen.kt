package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.data.model.OrderItem
import com.example.data.model.SaleOrderEntity
import com.example.ui.theme.FastFoodGreen
import com.example.ui.theme.FastFoodOrange
import com.example.util.CurrencyFormatter
import com.example.util.JsonHelper
import com.example.util.ReceiptPrinter

@Composable
fun SettingsScreen(
    currentProfile: BusinessProfile,
    onSaveProfile: (BusinessProfile) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(currentProfile.businessName) }
    var taxId by remember { mutableStateOf(currentProfile.taxId) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var address by remember { mutableStateOf(currentProfile.address) }
    var currencySymbol by remember { mutableStateOf(currentProfile.currencySymbol) }
    var taxRateStr by remember { mutableStateOf(if (currentProfile.taxRatePercent > 0) currentProfile.taxRatePercent.toString() else "0") }
    var header by remember { mutableStateOf(currentProfile.receiptHeader) }
    var footer by remember { mutableStateOf(currentProfile.receiptFooter) }
    var cashier by remember { mutableStateOf(currentProfile.cashierName) }
    var useThousandsSeparator by remember { mutableStateOf(currentProfile.useThousandsSeparator) }
    var thousandsSeparator by remember { mutableStateOf(currentProfile.thousandsSeparator) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Configuración del Negocio & Recibos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Personaliza los datos que aparecen impresos en cada recibo y comprobante de venta.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 1: Business Identity
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = FastFoodOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("DATOS DEL LOCAL / RESTAURANTE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del Negocio") },
                        placeholder = { Text("Ej. Burger & Dog House") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("biz_name_input")
                    )

                    OutlinedTextField(
                        value = taxId,
                        onValueChange = { taxId = it },
                        label = { Text("Identificación Fiscal (NIT / RUC / CIF / RFC)") },
                        placeholder = { Text("Ej. NIT 900.123.456-7") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Teléfono de Contacto") },
                            placeholder = { Text("+57 300 123 4567") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = cashier,
                            onValueChange = { cashier = it },
                            label = { Text("Nombre del Cajero") },
                            placeholder = { Text("Cajero 1") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección del Local") },
                        placeholder = { Text("Av. Principal # 12-34") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 2: Currency, Taxes & Thousands Separator Formatting
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = FastFoodOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("MONEDA, IMPUESTOS & FORMATO VISUAL", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = currencySymbol,
                            onValueChange = { currencySymbol = it },
                            label = { Text("Símbolo de Moneda") },
                            placeholder = { Text("$") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("biz_currency_input")
                        )

                        OutlinedTextField(
                            value = taxRateStr,
                            onValueChange = { input ->
                                val clean = input.filter { it.isDigit() || it == '.' }
                                taxRateStr = clean
                            },
                            label = { Text("Impuesto / IVA (%)") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Optional Thousands Separator Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { useThousandsSeparator = !useThousandsSeparator }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = "Separador de Miles (Visual)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Formatea visualmente los precios y totales. En la base de datos siempre se guarda el valor numérico puro sin separadores.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }

                        Switch(
                            checked = useThousandsSeparator,
                            onCheckedChange = { useThousandsSeparator = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = FastFoodOrange
                            ),
                            modifier = Modifier.testTag("thousands_separator_switch")
                        )
                    }

                    // Character Selector for Thousands Separator (Point vs Comma)
                    if (useThousandsSeparator) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Carácter de Separación de Miles:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Option 1: Punto (.)
                                OutlinedCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { thousandsSeparator = "." }
                                        .testTag("sep_point_option"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        width = if (thousandsSeparator == ".") 2.dp else 1.dp,
                                        color = if (thousandsSeparator == ".") FastFoodOrange else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = if (thousandsSeparator == ".") FastFoodOrange.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = thousandsSeparator == ".",
                                            onClick = { thousandsSeparator = "." },
                                            colors = RadioButtonDefaults.colors(selectedColor = FastFoodOrange)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text("Punto (.)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Ej: 15.000", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                // Option 2: Coma (,)
                                OutlinedCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { thousandsSeparator = "," }
                                        .testTag("sep_comma_option"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        width = if (thousandsSeparator == ",") 2.dp else 1.dp,
                                        color = if (thousandsSeparator == ",") FastFoodOrange else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = if (thousandsSeparator == ",") FastFoodOrange.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = thousandsSeparator == ",",
                                            onClick = { thousandsSeparator = "," },
                                            colors = RadioButtonDefaults.colors(selectedColor = FastFoodOrange)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text("Coma (,)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Ej: 15,000", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Live Preview Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF9F6F0)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FastFoodGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Vista Previa en Pantalla & Recibos:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))

                            val sym = currencySymbol.ifBlank { "$" }
                            val ex1 = CurrencyFormatter.format(14500.0, sym, useThousandsSeparator, thousandsSeparator)
                            val ex2 = CurrencyFormatter.format(120000.0, sym, useThousandsSeparator, thousandsSeparator)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Perro Especial: $ex1",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Total Venta: $ex2",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FastFoodOrange
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Receipt Customization
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = FastFoodOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TEXTOS DEL RECIBO TÉRMICO", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    OutlinedTextField(
                        value = header,
                        onValueChange = { header = it },
                        label = { Text("Eslogan / Encabezado del Recibo") },
                        placeholder = { Text("¡Los mejores perros calientes y hamburguesas!") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = footer,
                        onValueChange = { footer = it },
                        label = { Text("Mensaje de Pie de Recibo (Agradecimiento)") },
                        placeholder = { Text("¡Gracias por su visita! Vuelva pronto.") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 4: Actions & Test Print
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val tax = taxRateStr.toDoubleOrNull() ?: 0.0
                        val updated = currentProfile.copy(
                            businessName = name.trim().ifBlank { "Comida Rápida" },
                            taxId = taxId.trim(),
                            phone = phone.trim(),
                            address = address.trim(),
                            currencySymbol = currencySymbol.trim().ifBlank { "$" },
                            taxRatePercent = tax,
                            receiptHeader = header.trim(),
                            receiptFooter = footer.trim(),
                            cashierName = cashier.trim().ifBlank { "Cajero 1" },
                            useThousandsSeparator = useThousandsSeparator,
                            thousandsSeparator = thousandsSeparator
                        )
                        onSaveProfile(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = FastFoodOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Configuración", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // Test Print Button
                OutlinedButton(
                    onClick = {
                        val sampleItems = listOf(
                            OrderItem(1, "Perro Especial con Tocineta", "PERROS", 14500.0, 2, "Sin cebolla", "🌭"),
                            OrderItem(2, "Hamburguesa Doble Smash", "HAMBURGUESAS", 24500.0, 1, "Doble queso", "🍔"),
                            OrderItem(3, "Gaseosa 400ml", "BEBIDAS", 4500.0, 2, "Bien fría", "🥤")
                        )
                        val sub = sampleItems.sumOf { it.subtotal }
                        val sampleOrder = SaleOrderEntity(
                            orderNumber = 101,
                            customerName = "Cliente de Prueba",
                            orderType = "Comer Aquí",
                            tableOrAddress = "Mesa 5",
                            itemsJson = JsonHelper.orderItemsToJson(sampleItems),
                            subtotal = sub,
                            grandTotal = sub,
                            amountPaid = 65000.0,
                            changeAmount = 7000.0,
                            paymentMethod = "Efectivo",
                            cashierName = cashier.ifBlank { "Cajero 1" },
                            orderNotes = "Impresión de prueba para calibrar recibos."
                        )
                        val previewProfile = currentProfile.copy(
                            businessName = name.trim().ifBlank { "Comida Rápida" },
                            currencySymbol = currencySymbol.trim().ifBlank { "$" },
                            useThousandsSeparator = useThousandsSeparator,
                            thousandsSeparator = thousandsSeparator
                        )
                        ReceiptPrinter.printReceipt(context, sampleOrder, sampleItems, previewProfile)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("test_print_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Imprimir Recibo de Prueba")
                }
            }
        }
    }
}


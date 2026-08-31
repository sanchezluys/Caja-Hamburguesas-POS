package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FoodCategory
import com.example.util.CurrencyFormatter

@Composable
fun QuickAddDialog(
    currencySymbol: String,
    onAddItem: (name: String, price: Double, category: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.COMBOS_EXTRAS.id) }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Venta Rápida / Ítem Personalizado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Agrega rápidamente un valor o producto especial al pedido.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre / Concepto") },
                    placeholder = { Text("Ej. Adición especial, Combo del día") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quick_add_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() || it == '.' || it == ',' }
                        priceStr = clean
                    },
                    label = { Text("Precio ($currencySymbol)") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("quick_add_price_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Categoría:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair(FoodCategory.PERROS.id, "🌭 Perro"),
                        Pair(FoodCategory.HAMBURGUESAS.id, "🍔 Burger"),
                        Pair(FoodCategory.BEBIDAS.id, "🥤 Bebida"),
                        Pair(FoodCategory.COMBOS_EXTRAS.id, "🍟 Extra")
                    ).forEach { (catId, label) ->
                        FilterChip(
                            selected = selectedCategory == catId,
                            onClick = { selectedCategory = catId },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas / Detalles (opcional)") },
                    placeholder = { Text("Ej. Sin sal, salsa especial") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = CurrencyFormatter.parseInputAmount(priceStr)
                            val finalName = if (name.isBlank()) "Ítem Especial" else name
                            if (price > 0) {
                                onAddItem(finalName, price, selectedCategory, notes)
                                onDismiss()
                            }
                        },
                        enabled = CurrencyFormatter.parseInputAmount(priceStr) > 0.0,
                        modifier = Modifier.testTag("confirm_quick_add_button")
                    ) {
                        Text("Agregar al Pedido")
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BusinessProfile
import com.example.data.model.FoodCategory
import com.example.data.model.MenuItemEntity
import com.example.ui.theme.FastFoodOrange
import com.example.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
    menuItems: List<MenuItemEntity>,
    businessProfile: BusinessProfile,
    onSaveItem: (MenuItemEntity) -> Unit,
    onDeleteItem: (MenuItemEntity) -> Unit,
    onResetDefaults: () -> Unit
) {
    val sym = businessProfile.currencySymbol
    var editingItem by remember { mutableStateOf<MenuItemEntity?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<MenuItemEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Catálogo de Menú",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${menuItems.size} productos disponibles",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("reset_menu_defaults_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restaurar Todo", fontSize = 11.sp)
                    }
                }
            }

            // Group by category
            val grouped = menuItems.groupBy { it.category }
            listOf(
                Pair(FoodCategory.PERROS.id, "🌭 Perros Calientes"),
                Pair(FoodCategory.HAMBURGUESAS.id, "🍔 Hamburguesas"),
                Pair(FoodCategory.BEBIDAS.id, "🥤 Bebidas"),
                Pair(FoodCategory.COMBOS_EXTRAS.id, "🍟 Combos & Extras")
            ).forEach { (catKey, catTitle) ->
                val itemsInCat = grouped[catKey] ?: emptyList()
                if (itemsInCat.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$catTitle (${itemsInCat.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(itemsInCat, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(item.emoji, fontSize = 22.sp)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        if (item.description.isNotBlank()) {
                                            Text(
                                                text = item.description,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1
                                            )
                                        }
                                        Text(
                                            text = CurrencyFormatter.format(item.price, sym),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingItem = item },
                                        modifier = Modifier.size(34.dp).testTag("edit_product_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                    }

                                    IconButton(
                                        onClick = { itemToDelete = item },
                                        modifier = Modifier.size(34.dp).testTag("delete_product_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFDC2626))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add New Item
        FloatingActionButton(
            onClick = { isAddingNew = true },
            containerColor = FastFoodOrange,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_new_product_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
        }
    }

    // Add / Edit Dialog
    if (isAddingNew || editingItem != null) {
        val target = editingItem ?: MenuItemEntity(name = "", category = "PERROS", price = 0.0, emoji = "🌭")
        MenuItemEditDialog(
            item = target,
            isNew = isAddingNew,
            currencySymbol = sym,
            onDismiss = {
                isAddingNew = false
                editingItem = null
            },
            onSave = { saved ->
                onSaveItem(saved)
                isAddingNew = false
                editingItem = null
            }
        )
    }

    // Delete Item Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Deseas eliminar \"${item.name}\" del menú?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteItem(item)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Reset Defaults Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Restaurar Menú") },
            text = { Text("¿Restaurar todo el menú con la lista completa de perros calientes, hamburguesas y bebidas predefinidas?") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDefaults()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FastFoodOrange)
                ) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MenuItemEditDialog(
    item: MenuItemEntity,
    isNew: Boolean,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (MenuItemEntity) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var priceStr by remember { mutableStateOf(CurrencyFormatter.formatForInput(item.price)) }
    var category by remember { mutableStateOf(item.category) }
    var description by remember { mutableStateOf(item.description) }
    var emoji by remember { mutableStateOf(item.emoji) }

    val emojiPresets = when (category) {
        "PERROS" -> listOf("🌭", "🥖", "🥓", "🧀", "🌶️")
        "HAMBURGUESAS" -> listOf("🍔", "🥩", "🧀", "🍗", "🥪")
        "BEBIDAS" -> listOf("🥤", "🍹", "🍺", "🍋", "💧", "🧃")
        else -> listOf("🍟", "🧅", "🧀", "🥓", "🍿")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isNew) "Agregar Nuevo Producto" else "Editar Producto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Producto") },
                    placeholder = { Text("Ej. Perro Hawaiano Especial") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("menu_edit_name_input")
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
                    modifier = Modifier.fillMaxWidth().testTag("menu_edit_price_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Categoría:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        Pair(FoodCategory.PERROS.id, "🌭 Perros"),
                        Pair(FoodCategory.HAMBURGUESAS.id, "🍔 Burgers"),
                        Pair(FoodCategory.BEBIDAS.id, "🥤 Bebidas"),
                        Pair(FoodCategory.COMBOS_EXTRAS.id, "🍟 Extras")
                    ).forEach { (catId, catLabel) ->
                        FilterChip(
                            selected = category == catId,
                            onClick = {
                                category = catId
                                emoji = when (catId) {
                                    "PERROS" -> "🌭"
                                    "HAMBURGUESAS" -> "🍔"
                                    "BEBIDAS" -> "🥤"
                                    else -> "🍟"
                                }
                            },
                            label = { Text(catLabel, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Ícono Emoji:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojiPresets.forEach { em ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (emoji == em) FastFoodOrange.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { emoji = em }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(em, fontSize = 18.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción / Ingredientes") },
                    placeholder = { Text("Ej. Salchicha, tocineta, queso fundido...") },
                    maxLines = 2,
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
                            if (name.isNotBlank() && price > 0) {
                                onSave(
                                    item.copy(
                                        name = name.trim(),
                                        price = price,
                                        category = category,
                                        description = description.trim(),
                                        emoji = emoji
                                    )
                                )
                            }
                        },
                        enabled = name.isNotBlank() && CurrencyFormatter.parseInputAmount(priceStr) > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = FastFoodOrange),
                        modifier = Modifier.testTag("menu_save_button")
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

package com.example.data.db

import com.example.data.model.MenuItemEntity

object DefaultMenuData {
    fun getInitialMenu(): List<MenuItemEntity> = listOf(
        // Perros Calientes
        MenuItemEntity(
            id = 1,
            name = "Perro Clásico Sencillo",
            category = "PERROS",
            price = 9500.0,
            description = "Salchicha americana, ripio de papa, queso rallado, salsas de la casa y salsa tártara.",
            emoji = "🌭",
            displayOrder = 1
        ),
        MenuItemEntity(
            id = 2,
            name = "Perro Especial con Tocineta",
            category = "PERROS",
            price = 14500.0,
            description = "Salchicha premium envuelta en tocineta crujiente, queso fundido, ripio, salsas especiales.",
            emoji = "🌭",
            displayOrder = 2
        ),
        MenuItemEntity(
            id = 3,
            name = "Perro Suizo / Gratinado",
            category = "PERROS",
            price = 16000.0,
            description = "Salchicha suiza artesanal, doble queso mozzarella gratinado, tocineta y piña caramelizada.",
            emoji = "🌭",
            displayOrder = 3
        ),
        MenuItemEntity(
            id = 4,
            name = "Perro Mexicano Picante",
            category = "PERROS",
            price = 15500.0,
            description = "Salchicha de res, frijol refrito, guacamole fresco, jalapeños, queso cheddar y totopos.",
            emoji = "🌭",
            displayOrder = 4
        ),
        MenuItemEntity(
            id = 5,
            name = "Choriperro Gourmet",
            category = "PERROS",
            price = 17000.0,
            description = "Chorizo santarrosano asado al carbón, chimichurri artesanal, queso costeño y papitas.",
            emoji = "🌭",
            displayOrder = 5
        ),

        // Hamburguesas
        MenuItemEntity(
            id = 6,
            name = "Hamburguesa Clásica",
            category = "HAMBURGUESAS",
            price = 16500.0,
            description = "150g carne de res 100%, queso cheddar, lechuga fresca, tomate maduro y salsas.",
            emoji = "🍔",
            displayOrder = 6
        ),
        MenuItemEntity(
            id = 7,
            name = "Hamburguesa BBQ Bacon",
            category = "HAMBURGUESAS",
            price = 21000.0,
            description = "180g carne de res, doble tocineta ahumada, queso cheddar, cebolla caramelizada y salsa BBQ.",
            emoji = "🍔",
            displayOrder = 7
        ),
        MenuItemEntity(
            id = 8,
            name = "Hamburguesa Doble Smash",
            category = "HAMBURGUESAS",
            price = 24500.0,
            description = "2 carnes smash de 100g con costra crocante, doble queso americano, pepinillos y aderezo especial.",
            emoji = "🍔",
            displayOrder = 8
        ),
        MenuItemEntity(
            id = 9,
            name = "Hamburguesa Pollo Crispy",
            category = "HAMBURGUESAS",
            price = 19000.0,
            description = "Pechuga de pollo apanada ultra crocante, ensalada coleslaw, queso gouda y mayonesa de ajo.",
            emoji = "🍔",
            displayOrder = 9
        ),
        MenuItemEntity(
            id = 10,
            name = "Monster Burger Triple",
            category = "HAMBURGUESAS",
            price = 29500.0,
            description = "Triple carne de 120g, triple queso, huevo frito, tocineta crocante y aros de cebolla.",
            emoji = "🍔",
            displayOrder = 10
        ),

        // Bebidas
        MenuItemEntity(
            id = 11,
            name = "Gaseosa Personal (Lata/Pet)",
            category = "BEBIDAS",
            price = 4500.0,
            description = "Coca-Cola, Pepsi, Manzana Postobón, Colombiana, Sprite o Cuatro 400ml.",
            emoji = "🥤",
            displayOrder = 11
        ),
        MenuItemEntity(
            id = 12,
            name = "Gaseosa Familiar 1.5L",
            category = "BEBIDAS",
            price = 9000.0,
            description = "Botella familiar 1.5 Litros para compartir.",
            emoji = "🥤",
            displayOrder = 12
        ),
        MenuItemEntity(
            id = 13,
            name = "Jugo Natural Frappé",
            category = "BEBIDAS",
            price = 6500.0,
            description = "Mango, Maracuyá, Fresa, Lulo o Mora en agua o leche.",
            emoji = "🍹",
            displayOrder = 13
        ),
        MenuItemEntity(
            id = 14,
            name = "Cerveza Helada",
            category = "BEBIDAS",
            price = 6000.0,
            description = "Cerveza nacional fría (Club Colombia, Corona, Heineken, Aguila).",
            emoji = "🍺",
            displayOrder = 14
        ),
        MenuItemEntity(
            id = 15,
            name = "Limonada Natural / Cerezada",
            category = "BEBIDAS",
            price = 6000.0,
            description = "Limonada refrescante natural o con toque de cerezas maceradas.",
            emoji = "🍋",
            displayOrder = 15
        ),
        MenuItemEntity(
            id = 16,
            name = "Agua Mineral / Sin Gas",
            category = "BEBIDAS",
            price = 3500.0,
            description = "Botella de agua 600ml bien fría.",
            emoji = "💧",
            displayOrder = 16
        ),

        // Combos & Extras
        MenuItemEntity(
            id = 17,
            name = "Papas a la Francesa Clásicas",
            category = "COMBOS_EXTRAS",
            price = 7000.0,
            description = "Porción de papas doradas y crocantes con sal marina y salsas.",
            emoji = "🍟",
            displayOrder = 17
        ),
        MenuItemEntity(
            id = 18,
            name = "Papas Cheddar & Tocineta",
            category = "COMBOS_EXTRAS",
            price = 12000.0,
            description = "Papas francesas bañadas en salsa cheddar caliente y tocineta picada.",
            emoji = "🍟",
            displayOrder = 18
        ),
        MenuItemEntity(
            id = 19,
            name = "Aros de Cebolla Apanados",
            category = "COMBOS_EXTRAS",
            price = 8500.0,
            description = "Aros de cebolla crujientes acompañados de salsa BBQ.",
            emoji = "🧅",
            displayOrder = 19
        ),
        MenuItemEntity(
            id = 20,
            name = "Adición Queso / Tocineta Extra",
            category = "COMBOS_EXTRAS",
            price = 3500.0,
            description = "Extra tocineta ahumada o queso derretido para cualquier plato.",
            emoji = "🧀",
            displayOrder = 20
        )
    )
}

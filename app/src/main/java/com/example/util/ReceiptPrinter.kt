package com.example.util

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.data.model.BusinessProfile
import com.example.data.model.OrderItem
import com.example.data.model.SaleOrderEntity

object ReceiptPrinter {

    /**
     * Prints the receipt using Android's native PrintManager.
     * Works with all Android-compatible thermal printers, WiFi printers, Bluetooth print services, and PDF export.
     */
    fun printReceipt(
        context: Context,
        order: SaleOrderEntity,
        items: List<OrderItem>,
        business: BusinessProfile
    ) {
        val htmlDocument = generateHtmlReceipt(order, items, business)

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                if (printManager != null) {
                    val printAdapter: PrintDocumentAdapter = webView.createPrintDocumentAdapter("Recibo_Orden_${order.orderNumber}")
                    val printAttributes = PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A6) // Compact receipt standard
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()
                    printManager.print("Pedido #${order.orderNumber}", printAdapter, printAttributes)
                } else {
                    Toast.makeText(context, "Servicio de impresión no disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }
        webView.loadDataWithBaseURL(null, htmlDocument, "text/html", "UTF-8", null)
    }

    /**
     * Generates a beautifully formatted plain-text receipt for WhatsApp, Telegram, or SMS.
     */
    fun generateTextReceipt(
        order: SaleOrderEntity,
        items: List<OrderItem>,
        business: BusinessProfile
    ): String {
        val sym = business.currencySymbol
        val dateStr = CurrencyFormatter.formatDate(order.timestamp)
        val sb = StringBuilder()

        sb.append("================================\n")
        sb.append("   ${business.businessName.uppercase()}\n")
        if (business.taxId.isNotBlank()) sb.append("   ${business.taxId}\n")
        if (business.address.isNotBlank()) sb.append("   ${business.address}\n")
        if (business.phone.isNotBlank()) sb.append("   Tel: ${business.phone}\n")
        sb.append("================================\n")
        sb.append("RECIBO DE VENTA #${order.orderNumber}\n")
        sb.append("Fecha: $dateStr\n")
        sb.append("Atendido por: ${order.cashierName}\n")
        sb.append("Tipo: ${order.orderType}\n")
        if (order.customerName.isNotBlank() && order.customerName != "Cliente General") {
            sb.append("Cliente: ${order.customerName}\n")
        }
        if (order.tableOrAddress.isNotBlank()) {
            sb.append("Ref/Mesa: ${order.tableOrAddress}\n")
        }
        sb.append("--------------------------------\n")
        sb.append(String.format("%-4s %-16s %8s\n", "CANT", "PRODUCTO", "TOTAL"))
        sb.append("--------------------------------\n")

        for (item in items) {
            val itemName = if (item.name.length > 16) item.name.substring(0, 16) else item.name
            val itemTotal = CurrencyFormatter.format(item.subtotal, sym)
            sb.append(String.format("%-4d %-16s %8s\n", item.quantity, itemName, itemTotal))
            if (item.notes.isNotBlank()) {
                sb.append("     * ${item.notes}\n")
            }
        }

        sb.append("--------------------------------\n")
        sb.append(String.format("%-18s %12s\n", "Subtotal:", CurrencyFormatter.format(order.subtotal, sym)))
        if (order.discountAmount > 0) {
            sb.append(String.format("%-18s %12s\n", "Descuento:", "- " + CurrencyFormatter.format(order.discountAmount, sym)))
        }
        if (order.taxAmount > 0) {
            sb.append(String.format("%-18s %12s\n", "Impuesto (${order.taxPercent}%):", "+ " + CurrencyFormatter.format(order.taxAmount, sym)))
        }
        if (order.tipAmount > 0) {
            sb.append(String.format("%-18s %12s\n", "Propina:", "+ " + CurrencyFormatter.format(order.tipAmount, sym)))
        }
        sb.append("================================\n")
        sb.append(String.format("%-16s %14s\n", "TOTAL A PAGAR:", CurrencyFormatter.format(order.grandTotal, sym)))
        sb.append("================================\n")
        sb.append("Método de Pago: ${order.paymentMethod}\n")
        if (order.paymentMethod == "Efectivo") {
            sb.append(String.format("%-18s %12s\n", "Recibido:", CurrencyFormatter.format(order.amountPaid, sym)))
            sb.append(String.format("%-18s %12s\n", "Cambio/Vuelto:", CurrencyFormatter.format(order.changeAmount, sym)))
        }
        if (order.orderNotes.isNotBlank()) {
            sb.append("Notas: ${order.orderNotes}\n")
        }
        sb.append("--------------------------------\n")
        if (business.receiptFooter.isNotBlank()) {
            sb.append("   ${business.receiptFooter}\n")
        }
        sb.append("================================\n")

        return sb.toString()
    }

    /**
     * Shares the receipt via Android Intent (WhatsApp, Messages, Email, etc.).
     */
    fun shareReceipt(context: Context, order: SaleOrderEntity, items: List<OrderItem>, business: BusinessProfile) {
        val text = generateTextReceipt(order, items, business)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Recibo de Compra #${order.orderNumber} - ${business.businessName}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Enviar Recibo de Venta")
        context.startActivity(shareIntent)
    }

    /**
     * Generates a thermal-style HTML document for rendering and printing.
     */
    fun generateHtmlReceipt(
        order: SaleOrderEntity,
        items: List<OrderItem>,
        business: BusinessProfile
    ): String {
        val sym = business.currencySymbol
        val dateStr = CurrencyFormatter.formatDate(order.timestamp)

        val itemsRows = StringBuilder()
        for (item in items) {
            itemsRows.append("""
                <tr>
                    <td style="text-align: left; padding: 4px 0;">
                        <strong>${item.quantity}x ${item.name}</strong>
                        ${if (item.notes.isNotBlank()) "<div style='font-size: 11px; color: #555; font-style: italic;'>* ${item.notes}</div>" else ""}
                    </td>
                    <td style="text-align: right; padding: 4px 0; vertical-align: top; white-space: nowrap;">
                        ${CurrencyFormatter.format(item.subtotal, sym)}
                    </td>
                </tr>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: 'Courier New', Courier, monospace;
                        margin: 0;
                        padding: 12px;
                        color: #000;
                        background: #fff;
                        width: 100%;
                        max-width: 380px;
                        font-size: 13px;
                        line-height: 1.35;
                    }
                    .text-center { text-align: center; }
                    .text-right { text-align: right; }
                    .text-left { text-align: left; }
                    .bold { font-weight: bold; }
                    .divider {
                        border-top: 1px dashed #000;
                        margin: 8px 0;
                    }
                    .double-divider {
                        border-top: 2px dashed #000;
                        margin: 10px 0;
                    }
                    .business-name {
                        font-size: 18px;
                        font-weight: bold;
                        letter-spacing: 0.5px;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                    }
                    .total-box {
                        font-size: 17px;
                        font-weight: bold;
                        padding: 6px 0;
                    }
                    .footer {
                        margin-top: 12px;
                        font-size: 12px;
                        color: #333;
                    }
                </style>
            </head>
            <body>
                <div class="text-center">
                    <div class="business-name">${business.businessName}</div>
                    ${if (business.receiptHeader.isNotBlank()) "<div>${business.receiptHeader}</div>" else ""}
                    ${if (business.taxId.isNotBlank()) "<div>${business.taxId}</div>" else ""}
                    ${if (business.address.isNotBlank()) "<div>${business.address}</div>" else ""}
                    ${if (business.phone.isNotBlank()) "<div>Tel: ${business.phone}</div>" else ""}
                </div>

                <div class="double-divider"></div>

                <div style="display: flex; justify-content: space-between;">
                    <span><strong>ORDEN #:</strong> ${order.orderNumber}</span>
                    <span><strong>TIPO:</strong> ${order.orderType}</span>
                </div>
                <div><strong>FECHA:</strong> $dateStr</div>
                <div><strong>CAJERO:</strong> ${order.cashierName}</div>
                ${if (order.customerName.isNotBlank() && order.customerName != "Cliente General") "<div><strong>CLIENTE:</strong> ${order.customerName}</div>" else ""}
                ${if (order.tableOrAddress.isNotBlank()) "<div><strong>MESA/DIR:</strong> ${order.tableOrAddress}</div>" else ""}

                <div class="divider"></div>

                <table>
                    <thead>
                        <tr style="border-bottom: 1px dashed #000;">
                            <th class="text-left" style="padding-bottom: 4px;">DESCRIPCIÓN</th>
                            <th class="text-right" style="padding-bottom: 4px;">TOTAL</th>
                        </tr>
                    </thead>
                    <tbody>
                        $itemsRows
                    </tbody>
                </table>

                <div class="divider"></div>

                <table>
                    <tr>
                        <td class="text-left">Subtotal:</td>
                        <td class="text-right">${CurrencyFormatter.format(order.subtotal, sym)}</td>
                    </tr>
                    ${if (order.discountAmount > 0) """
                    <tr>
                        <td class="text-left">Descuento:</td>
                        <td class="text-right">- ${CurrencyFormatter.format(order.discountAmount, sym)}</td>
                    </tr>
                    """ else ""}
                    ${if (order.taxAmount > 0) """
                    <tr>
                        <td class="text-left">Impuesto (${order.taxPercent}%):</td>
                        <td class="text-right">+ ${CurrencyFormatter.format(order.taxAmount, sym)}</td>
                    </tr>
                    """ else ""}
                    ${if (order.tipAmount > 0) """
                    <tr>
                        <td class="text-left">Propina:</td>
                        <td class="text-right">+ ${CurrencyFormatter.format(order.tipAmount, sym)}</td>
                    </tr>
                    """ else ""}
                </table>

                <div class="double-divider"></div>

                <table class="total-box">
                    <tr>
                        <td class="text-left">TOTAL:</td>
                        <td class="text-right">${CurrencyFormatter.format(order.grandTotal, sym)}</td>
                    </tr>
                </table>

                <div class="double-divider"></div>

                <table>
                    <tr>
                        <td class="text-left">Método de Pago:</td>
                        <td class="text-right"><strong>${order.paymentMethod}</strong></td>
                    </tr>
                    ${if (order.paymentMethod == "Efectivo") """
                    <tr>
                        <td class="text-left">Recibido:</td>
                        <td class="text-right">${CurrencyFormatter.format(order.amountPaid, sym)}</td>
                    </tr>
                    <tr>
                        <td class="text-left"><strong>CAMBIO / VUELTO:</strong></td>
                        <td class="text-right"><strong>${CurrencyFormatter.format(order.changeAmount, sym)}</strong></td>
                    </tr>
                    """ else ""}
                </table>

                ${if (order.orderNotes.isNotBlank()) """
                <div class="divider"></div>
                <div><strong>Notas:</strong> ${order.orderNotes}</div>
                """ else ""}

                <div class="double-divider"></div>

                <div class="text-center footer">
                    <div>${business.receiptFooter}</div>
                    <div style="margin-top: 6px; font-size: 10px; color: #888;">Generado con Caja Rápida POS</div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}

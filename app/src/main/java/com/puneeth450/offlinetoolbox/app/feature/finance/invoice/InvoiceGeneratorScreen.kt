package com.puneeth450.offlinetoolbox.app.feature.finance.invoice

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

private data class InvoiceProduct(
    val name: String,
    val quantity: Int,
    val pricePerUnit: Double,
    val discount: Double,
    val discountIsFixed: Boolean
) {
    val gross: Double get() = quantity * pricePerUnit
    val discountAmount: Double get() = if (discountIsFixed) discount else gross * discount / 100.0
    val total: Double get() = max(0.0, gross - discountAmount)
}

private data class InvoiceCurrency(
    val symbol: String,
    val name: String,
    val code: String
)

private val InvoiceCurrencies = listOf(
    InvoiceCurrency("₹", "Indian Rupee", "INR"),
    InvoiceCurrency("$", "US Dollar", "USD"),
    InvoiceCurrency("€", "Euro", "EUR"),
    InvoiceCurrency("£", "British Pound", "GBP"),
    InvoiceCurrency("¥", "Japanese Yen", "JPY"),
    InvoiceCurrency("¥", "Chinese Yuan", "CNY"),
    InvoiceCurrency("A$", "Australian Dollar", "AUD"),
    InvoiceCurrency("C$", "Canadian Dollar", "CAD"),
    InvoiceCurrency("Fr", "Swiss Franc", "CHF"),
    InvoiceCurrency("S$", "Singapore Dollar", "SGD"),
    InvoiceCurrency("د.إ", "UAE Dirham", "AED"),
    InvoiceCurrency("﷼", "Saudi Riyal", "SAR"),
    InvoiceCurrency("RM", "Malaysian Ringgit", "MYR"),
    InvoiceCurrency("฿", "Thai Baht", "THB"),
    InvoiceCurrency("Rp", "Indonesian Rupiah", "IDR"),
    InvoiceCurrency("₱", "Philippine Peso", "PHP"),
    InvoiceCurrency("₫", "Vietnamese Dong", "VND"),
    InvoiceCurrency("₩", "Korean Won", "KRW"),
    InvoiceCurrency("R$", "Brazilian Real", "BRL"),
    InvoiceCurrency("Mex$", "Mexican Peso", "MXN"),
    InvoiceCurrency("R", "South African Rand", "ZAR"),
    InvoiceCurrency("₽", "Russian Ruble", "RUB"),
    InvoiceCurrency("₺", "Turkish Lira", "TRY"),
    InvoiceCurrency("zł", "Polish Zloty", "PLN"),
    InvoiceCurrency("NZ$", "New Zealand Dollar", "NZD")
)

private data class InvoiceRecord(
    val id: String,
    val date: String,
    val shopName: String,
    val shopAddress: String,
    val customerName: String,
    val currency: InvoiceCurrency,
    val products: List<InvoiceProduct>,
    val overallDiscount: Double,
    val taxEnabled: Boolean,
    val taxName: String,
    val taxRate: Double
) {
    val subtotal: Double get() = products.sumOf { it.total }
    val tax: Double get() = if (taxEnabled) max(0.0, subtotal - overallDiscount) * taxRate / 100.0 else 0.0
    val total: Double get() = max(0.0, subtotal - overallDiscount) + tax
}

@Composable
fun InvoiceGeneratorScreen(onNavigateBack: () -> Unit) {
    var showingHistory by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<InvoiceRecord>() }

    if (showingHistory) {
        InvoiceHistoryContent(
            invoices = history,
            onNavigateBack = { showingHistory = false },
            onDelete = { history.remove(it) }
        )
    } else {
        InvoiceGeneratorContent(
            onNavigateBack = onNavigateBack,
            onHistoryClick = { showingHistory = true },
            onInvoiceGenerated = { history.add(0, it) }
        )
    }
}

@Composable
private fun InvoiceGeneratorContent(
    onNavigateBack: () -> Unit,
    onHistoryClick: () -> Unit,
    onInvoiceGenerated: (InvoiceRecord) -> Unit
) {
    val context = LocalContext.current
    var shopName by remember { mutableStateOf("") }
    var shopAddress by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var overallDiscount by remember { mutableStateOf("") }
    var taxEnabled by remember { mutableStateOf(false) }
    var taxName by remember { mutableStateOf("GST") }
    var taxRate by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(InvoiceCurrencies.first()) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    val products = remember { mutableStateListOf<InvoiceProduct>() }
    var showProductDialog by remember { mutableStateOf(false) }
    var previewInvoice by remember { mutableStateOf<InvoiceRecord?>(null) }
    val currentInvoice by remember {
        derivedStateOf {
            InvoiceRecord(
                id = invoiceId(),
                date = displayDate(),
                shopName = shopName.ifBlank { "Shop Name" },
                shopAddress = shopAddress,
                customerName = customerName.ifBlank { "Customer" },
                currency = selectedCurrency,
                products = products.toList(),
                overallDiscount = overallDiscount.toDoubleOrNull() ?: 0.0,
                taxEnabled = taxEnabled,
                taxName = taxName.ifBlank { "Tax" },
                taxRate = taxRate.toDoubleOrNull() ?: 0.0
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding)
    ) {
        CommonTopBar(
            title = "Invoice Generator",
            onNavigateBack = onNavigateBack,
            actionIcon = Icons.Default.History,
            actionDescription = "Invoice history",
            onActionClick = onHistoryClick
        )

        Spacer(Modifier.height(28.dp))

        CurrencyPill(
            currency = selectedCurrency,
            onClick = { showCurrencyDialog = true }
        )

        Spacer(Modifier.height(28.dp))
        SectionLabel("Business Details")
        InvoiceTextField(value = shopName, onValueChange = { shopName = it }, label = "Shop Name *")
        Spacer(Modifier.height(14.dp))
        InvoiceTextField(value = shopAddress, onValueChange = { shopAddress = it }, label = "Shop Address")

        Spacer(Modifier.height(24.dp))
        SectionLabel("Customer Details")
        InvoiceTextField(value = customerName, onValueChange = { customerName = it }, label = "Customer Name")

        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionLabel("Products (${products.size})", modifier = Modifier.weight(1f))
            Button(
                onClick = { showProductDialog = true },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(12.dp))
        if (products.isEmpty()) {
            EmptyProductsCard()
        } else {
            products.forEach { product ->
                ProductRow(
                    product = product,
                    currency = selectedCurrency,
                    onDelete = { products.remove(product) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionLabel("Discount & Tax")
        InvoiceTextField(
            value = overallDiscount,
            onValueChange = { overallDiscount = it },
            label = "Overall Discount (${selectedCurrency.symbol})",
            keyboardType = KeyboardType.Decimal
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 14.dp)) {
            Checkbox(checked = taxEnabled, onCheckedChange = { taxEnabled = it })
            Text("Enable Tax", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
        }
        if (taxEnabled) {
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InvoiceTextField(
                    value = taxName,
                    onValueChange = { taxName = it },
                    label = "Tax Name",
                    modifier = Modifier.weight(1f)
                )
                InvoiceTextField(
                    value = taxRate,
                    onValueChange = { taxRate = it },
                    label = "%",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(0.28f)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        InvoiceSummary(currentInvoice)

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { previewInvoice = currentInvoice },
            enabled = shopName.isNotBlank() && products.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4A83))
        ) {
            Text("Preview Invoice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(18.dp))
        InvoiceAdCard()
    }

    if (showCurrencyDialog) {
        CurrencyDialog(
            selectedCurrency = selectedCurrency,
            onSelect = {
                selectedCurrency = it
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    if (showProductDialog) {
        AddProductDialog(
            currency = selectedCurrency,
            onDismiss = { showProductDialog = false },
            onSave = {
                products.add(it)
                showProductDialog = false
            }
        )
    }

    previewInvoice?.let { invoice ->
        InvoicePreviewDialog(
            invoice = invoice,
            onDismiss = { previewInvoice = null },
            onGeneratePdf = {
                val exportedInvoice = invoice.copy(id = invoiceId(), date = displayDate())
                exportAndShareInvoicePdf(
                    context = context,
                    invoice = exportedInvoice,
                    onSuccess = { onInvoiceGenerated(exportedInvoice) }
                )
                previewInvoice = null
            }
        )
    }
}

@Composable
private fun InvoiceHistoryContent(
    invoices: List<InvoiceRecord>,
    onNavigateBack: () -> Unit,
    onDelete: (InvoiceRecord) -> Unit
) {
    val context = LocalContext.current
    var previewInvoice by remember { mutableStateOf<InvoiceRecord?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding)
    ) {
        CommonTopBar(title = "Invoice History", onNavigateBack = onNavigateBack)
        if (invoices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No invoices yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Generated invoices will appear here", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Spacer(Modifier.height(56.dp))
            invoices.forEach { invoice ->
                InvoiceHistoryCard(
                    invoice = invoice,
                    onView = { previewInvoice = invoice },
                    onDelete = { onDelete(invoice) }
                )
                Spacer(Modifier.height(24.dp))
            }
            InvoiceAdCard()
        }
    }

    previewInvoice?.let { invoice ->
        InvoicePreviewDialog(
            invoice = invoice,
            onDismiss = { previewInvoice = null },
            onGeneratePdf = {
                exportAndShareInvoicePdf(context = context, invoice = invoice)
                previewInvoice = null
            }
        )
    }
}

@Composable
private fun CurrencyPill(currency: InvoiceCurrency, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
        color = Color.Transparent
    ) {
        Text(
            text = "${currency.symbol} ${currency.name} (${currency.code})",
            modifier = Modifier.padding(vertical = 14.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF2F4A83),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CurrencyDialog(
    selectedCurrency: InvoiceCurrency,
    onSelect: (InvoiceCurrency) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Select Currency", style = MaterialTheme.typography.headlineSmall)
                Column(
                    modifier = Modifier
                        .height(560.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    InvoiceCurrencies.forEach { currency ->
                        CurrencyOption(
                            currency = currency,
                            selected = currency == selectedCurrency,
                            onClick = { onSelect(currency) }
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyOption(currency: InvoiceCurrency, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currency.symbol,
            style = MaterialTheme.typography.headlineSmall,
            color = if (selected) Color(0xFF2F4A83) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.26f)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(currency.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(currency.code, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun InvoiceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    )
}

@Composable
private fun EmptyProductsCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("No products added yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ProductRow(product: InvoiceProduct, currency: InvoiceCurrency, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${product.quantity} × ${formatMoney(product.pricePerUnit, currency)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(formatMoney(product.total, currency), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2F4A83))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete product")
            }
        }
    }
}

@Composable
private fun InvoiceSummary(invoice: InvoiceRecord) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF2F4A83)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryLine("Subtotal", invoice.subtotal, Color.White.copy(alpha = 0.78f), currency = invoice.currency)
            if (invoice.overallDiscount > 0.0) SummaryLine("Discount", -invoice.overallDiscount, Color.White.copy(alpha = 0.78f), currency = invoice.currency)
            if (invoice.tax > 0.0) SummaryLine("${invoice.taxName} (${trimPercent(invoice.taxRate)}%)", invoice.tax, Color.White.copy(alpha = 0.78f), currency = invoice.currency)
            Surface(modifier = Modifier.fillMaxWidth().height(1.dp), color = Color.White.copy(alpha = 0.14f)) {}
            SummaryLine("Total", invoice.total, Color.White, bold = true, currency = invoice.currency)
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    amount: Double,
    color: Color,
    bold: Boolean = false,
    currency: InvoiceCurrency = InvoiceCurrencies.first()
) {
    Row {
        Text(label, modifier = Modifier.weight(1f), color = color, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(formatMoney(amount, currency), color = color, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun AddProductDialog(currency: InvoiceCurrency, onDismiss: () -> Unit, onSave: (InvoiceProduct) -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("0") }
    var fixedDiscount by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Add Product", style = MaterialTheme.typography.headlineSmall)
                InvoiceTextField(name, { name = it }, "Product Name")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InvoiceTextField(quantity, { quantity = it }, "Quantity", KeyboardType.Number, Modifier.weight(1f))
                    InvoiceTextField(price, { price = it }, "Price/Unit (${currency.symbol})", KeyboardType.Decimal, Modifier.weight(1f))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        InvoiceTextField(discount, { discount = it }, "Discount (${if (fixedDiscount) currency.symbol else "%"})", KeyboardType.Decimal)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (fixedDiscount) currency.symbol else "%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Switch(checked = fixedDiscount, onCheckedChange = { fixedDiscount = it })
                    }
                }
                Text("Discount as ${if (fixedDiscount) "fixed amount" else "percentage"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", fontWeight = FontWeight.Bold) }
                    Button(
                        onClick = {
                            onSave(
                                InvoiceProduct(
                                    name = name,
                                    quantity = quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                                    pricePerUnit = price.toDoubleOrNull() ?: 0.0,
                                    discount = discount.toDoubleOrNull() ?: 0.0,
                                    discountIsFixed = fixedDiscount
                                )
                            )
                        },
                        enabled = name.isNotBlank() && (price.toDoubleOrNull() ?: 0.0) > 0.0,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4A83))
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoicePreviewDialog(invoice: InvoiceRecord, onDismiss: () -> Unit, onGeneratePdf: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(modifier = Modifier.padding(28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Invoice Preview", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(invoice.shopName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (invoice.shopAddress.isNotBlank()) Text(invoice.shopAddress, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(invoice.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(invoice.date, color = MaterialTheme.colorScheme.onSurfaceVariant)
                DividerLine()
                Text("Bill To:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(invoice.customerName)
                DividerLine()
                Text("Products", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                invoice.products.forEach { product ->
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.46f)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${product.quantity} × ${formatMoney(product.pricePerUnit, invoice.currency)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (product.discountAmount > 0.0) Text("Discount: ${formatMoney(product.discountAmount, invoice.currency)}", color = MaterialTheme.colorScheme.error)
                            }
                            Text(formatMoney(product.total, invoice.currency), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2F4A83))
                        }
                    }
                }
                DividerLine()
                PreviewTotalLine("Subtotal", invoice.subtotal, invoice.currency)
                if (invoice.overallDiscount > 0.0) PreviewTotalLine("Discount", -invoice.overallDiscount, invoice.currency)
                if (invoice.tax > 0.0) PreviewTotalLine(invoice.taxName, invoice.tax, invoice.currency)
                DividerLine()
                PreviewTotalLine("Total", invoice.total, invoice.currency, bold = true)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Close", fontWeight = FontWeight.Bold) }
                    Button(
                        onClick = onGeneratePdf,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4A83))
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Text("Generate PDF", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewTotalLine(label: String, amount: Double, currency: InvoiceCurrency, bold: Boolean = false) {
    Row {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(formatMoney(amount, currency), style = MaterialTheme.typography.titleMedium, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = if (bold) Color(0xFF2F4A83) else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun DividerLine() {
    Surface(modifier = Modifier.fillMaxWidth().height(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.42f)) {}
}

@Composable
private fun InvoiceHistoryCard(invoice: InvoiceRecord, onView: () -> Unit, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(invoice.id, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(invoice.date, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(formatMoney(invoice.total, invoice.currency), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF2F4A83))
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onView, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Text("View", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = {}) { Icon(Icons.Default.Share, contentDescription = "Share invoice") }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete invoice") }
            }
        }
    }
}

@Composable
private fun InvoiceAdCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Ad", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text("Test Ad : North Island Scaffold Experts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Providing safe, practical business services for generated invoice workflows.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F4A83))) {
                Text("Open", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun invoiceId(): String = "INV-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())

private fun displayDate(): String = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())

private fun formatMoney(value: Double, currency: InvoiceCurrency): String {
    val sign = if (value < 0.0) "-" else ""
    return sign + currency.symbol + String.format(Locale.getDefault(), "%.2f", kotlin.math.abs(value))
}

private fun trimPercent(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.getDefault(), "%.2f", value)
    }
}

private fun exportAndShareInvoicePdf(
    context: Context,
    invoice: InvoiceRecord,
    onSuccess: () -> Unit = {}
) {
    runCatching {
        val file = createInvoicePdf(context, invoice)
        onSuccess()
        shareInvoicePdf(context, file)
        Toast.makeText(context, "PDF exported", Toast.LENGTH_SHORT).show()
    }.onFailure {
        Toast.makeText(context, "PDF export failed", Toast.LENGTH_SHORT).show()
    }
}

private fun createInvoicePdf(context: Context, invoice: InvoiceRecord): File {
    val invoicesDir = File(context.cacheDir, "invoices").apply { mkdirs() }
    val file = File(invoicesDir, "${invoice.id}.pdf")
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(30, 32, 40)
        textSize = 14f
    }
    val mutedPaint = Paint(paint).apply {
        color = android.graphics.Color.rgb(88, 92, 105)
    }
    val accentPaint = Paint(paint).apply {
        color = android.graphics.Color.rgb(47, 74, 131)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val errorPaint = Paint(paint).apply {
        color = android.graphics.Color.rgb(198, 40, 40)
    }
    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(145, 148, 158)
        strokeWidth = 1.3f
    }
    var y = 54f

    fun text(value: String, x: Float, size: Float = 14f, target: Paint = paint, bold: Boolean = false) {
        target.textSize = size
        target.typeface = Typeface.create(Typeface.DEFAULT, if (bold) Typeface.BOLD else Typeface.NORMAL)
        canvas.drawText(value, x, y, target)
        y += size + 8f
    }

    fun rightText(value: String, right: Float, baseline: Float, size: Float, target: Paint = paint, bold: Boolean = false) {
        target.textSize = size
        target.typeface = Typeface.create(Typeface.DEFAULT, if (bold) Typeface.BOLD else Typeface.NORMAL)
        canvas.drawText(value, right - target.measureText(value), baseline, target)
    }

    fun divider(extraTop: Float = 8f) {
        y += extraTop
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 24f
    }

    text("Invoice", 40f, size = 28f, target = accentPaint, bold = true)
    text(invoice.shopName, 40f, size = 20f, bold = true)
    if (invoice.shopAddress.isNotBlank()) text(invoice.shopAddress, 40f, target = mutedPaint)
    text(invoice.id, 40f, target = mutedPaint, bold = true)
    text(invoice.date, 40f, target = mutedPaint)
    divider()

    text("Bill To:", 40f, size = 16f, bold = true)
    text(invoice.customerName, 40f)
    divider()

    text("Products", 40f, size = 16f, bold = true)
    invoice.products.forEach { product ->
        val itemTop = y
        text(product.name, 54f, size = 15f, bold = true)
        text("${product.quantity} x ${formatMoney(product.pricePerUnit, invoice.currency)}", 54f, target = mutedPaint)
        if (product.discountAmount > 0.0) {
            text("Discount: ${formatMoney(product.discountAmount, invoice.currency)}", 54f, target = errorPaint)
        }
        rightText(formatMoney(product.total, invoice.currency), 540f, itemTop + 15f, 16f, accentPaint, bold = true)
        y += 6f
    }
    divider()

    val subtotalY = y
    text("Subtotal", 40f, size = 16f)
    rightText(formatMoney(invoice.subtotal, invoice.currency), 540f, subtotalY, 16f)
    if (invoice.overallDiscount > 0.0) {
        val rowY = y
        text("Discount", 40f, size = 16f)
        rightText(formatMoney(-invoice.overallDiscount, invoice.currency), 540f, rowY, 16f)
    }
    if (invoice.tax > 0.0) {
        val rowY = y
        text("${invoice.taxName} (${trimPercent(invoice.taxRate)}%)", 40f, size = 16f)
        rightText(formatMoney(invoice.tax, invoice.currency), 540f, rowY, 16f)
    }
    divider(extraTop = 0f)
    val totalY = y
    text("Total", 40f, size = 22f, bold = true)
    rightText(formatMoney(invoice.total, invoice.currency), 540f, totalY, 22f, accentPaint, bold = true)

    document.finishPage(page)
    file.outputStream().use { document.writeTo(it) }
    document.close()
    return file
}

private fun shareInvoicePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share invoice PDF"))
}

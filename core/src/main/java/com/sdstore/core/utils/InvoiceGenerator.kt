package com.sdstore.core.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.sdstore.core.models.Order
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object InvoiceGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40

    fun createInvoice(context: Context, order: Order): Boolean {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 24f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            isAntiAlias = true
            textSize = 14f
            isFakeBoldText = true
        }
        val textPaint = Paint().apply {
            isAntiAlias = true
            textSize = 12f
        }

        var yPosition = MARGIN

        canvas.drawText("Invoice - Saddam Store", MARGIN.toFloat(), yPosition.toFloat(), titlePaint)
        yPosition += 40

        val date = order.createdAt?.let { SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(it) } ?: "N/A"
        canvas.drawText("Order ID: ${order.orderId}", MARGIN.toFloat(), yPosition.toFloat(), headerPaint)
        yPosition += 20
        canvas.drawText("Date: $date", MARGIN.toFloat(), yPosition.toFloat(), textPaint)
        yPosition += 40

        canvas.drawText("Item", MARGIN.toFloat(), yPosition.toFloat(), headerPaint)
        canvas.drawText("Qty", (PAGE_WIDTH * 0.6).toFloat(), yPosition.toFloat(), headerPaint)
        canvas.drawText("Price", (PAGE_WIDTH * 0.8).toFloat(), yPosition.toFloat(), headerPaint)
        yPosition += 25
        canvas.drawLine(MARGIN.toFloat(), yPosition.toFloat(), (PAGE_WIDTH - MARGIN).toFloat(), yPosition.toFloat(), textPaint)
        yPosition += 20

        val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
        for (item in order.items) {
            canvas.drawText(item.name, MARGIN.toFloat(), yPosition.toFloat(), textPaint)
            canvas.drawText(item.quantity.toString(), (PAGE_WIDTH * 0.6).toFloat(), yPosition.toFloat(), textPaint)
            val priceString = format.format(item.pricePaisas / 100.0)
            canvas.drawText(priceString, (PAGE_WIDTH * 0.8).toFloat(), yPosition.toFloat(), textPaint)
            yPosition += 20
        }

        yPosition += 10
        canvas.drawLine(MARGIN.toFloat(), yPosition.toFloat(), (PAGE_WIDTH - MARGIN).toFloat(), yPosition.toFloat(), textPaint)
        yPosition += 30

        val totalText = "Total: ${format.format(order.totalPrice / 100.0)}"
        val totalPaint = Paint(headerPaint).apply { textSize = 18f }
        canvas.drawText(totalText, (PAGE_WIDTH - MARGIN - totalPaint.measureText(totalText)), yPosition.toFloat(), totalPaint)

        document.finishPage(page)

        val fileName = "Invoice-${order.orderId}.pdf"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        return try {
            document.writeTo(FileOutputStream(file))
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            document.close()
        }
    }
}
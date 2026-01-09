package com.example.himnariobeta.utils

import android.content.Context
import android.content.Intent
import com.example.himnariobeta.HymnEntity
import com.example.himnariobeta.HymnListEntity

// Compartir himno individual
fun shareHymn(context: Context, hymn: HymnEntity) {
    val sb = StringBuilder()
    sb.append("Nº ${hymn.numero ?: "S/N"}\n")
    sb.append("Título: ${hymn.title?.trim() ?: "Sin título"}\n")
    sb.append("----------------\n")
    sb.append(hymn.lyrics?.trim() ?: "Sin letra")
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Compartir himno vía")
    context.startActivity(shareIntent)
}

// Exportar himno como PDF
fun exportHymnAsPdf(context: Context, hymn: HymnEntity) {
    val pdfDocument = android.graphics.pdf.PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas
    val paint = android.graphics.Paint()
    val margin = 40f
    var y = 60f
    
    // Título del himno
    paint.textSize = 18f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    canvas.drawText("Nº ${hymn.numero ?: "S/N"}", margin, y, paint)
    y += 30f
    
    paint.textSize = 16f
    val title = hymn.title?.trim() ?: "Sin título"
    canvas.drawText("Título: $title", margin, y, paint)
    y += 30f
    
    y += 10f
    paint.typeface = android.graphics.Typeface.DEFAULT
    paint.textSize = 14f
    
    // Letra del himno
    val lyrics = hymn.lyrics?.trim() ?: "Sin letra"
    val lines = lyrics.split('\n')
    var pageNum = 1
    
    for (line in lines) {
        if (y > pageHeight - 60f) {
            pdfDocument.finishPage(page)
            pageNum++
            val newPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            page = pdfDocument.startPage(newPageInfo)
            canvas = page.canvas
            y = 60f
        }
        canvas.drawText(line, margin, y, paint)
        y += 22f
    }
    
    pdfDocument.finishPage(page)
    
    val safeTitle = hymn.title?.trim()?.replace(Regex("[^\\p{L}\\p{N}\\s]"), "")?.take(50) ?: "Sin_titulo"
    val fileName = "${hymn.numero ?: "sn"}. $safeTitle.pdf"
    val file = java.io.File(context.cacheDir, fileName)
    try {
        pdfDocument.writeTo(java.io.FileOutputStream(file))
        pdfDocument.close()
        
        val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartir himno (PDF)"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Función para compartir texto
fun shareListAsText(context: Context, list: HymnListEntity, hymns: List<HymnEntity>) {
    val sb = StringBuilder()
    sb.append("LISTA: ${list.name}\n")
    sb.append("----------------\n\n")

    hymns.forEachIndexed { index, hymn ->
        sb.append("${index + 1}. Nº ${hymn.numero ?: "S/N"} - ${hymn.title?.trim() ?: "Sin título"}\n")
        sb.append(hymn.lyrics?.trim() ?: "Sin letra")
        sb.append("\n\n")
    }

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Compartir lista vía")
    context.startActivity(shareIntent)
}

// Exportar lista como PDF
fun exportListAsPdf(context: Context, list: HymnListEntity, hymns: List<HymnEntity>) {
    val pdfDocument = android.graphics.pdf.PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val margin = 40f
    var pageNum = 1
    
    var pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas
    val paint = android.graphics.Paint()
    var y = 60f
    
    // Título de la lista
    paint.textSize = 18f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    canvas.drawText("LISTA: ${list.name}", margin, y, paint)
    y += 30f
    
    paint.textSize = 14f
    paint.typeface = android.graphics.Typeface.DEFAULT
    y += 10f
    
    // Himnos en la lista
    hymns.forEachIndexed { index, hymn ->
        if (y > pageHeight - 100f) {
            pdfDocument.finishPage(page)
            pageNum++
            pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            y = 60f
        }
        
        paint.textSize = 16f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("${index + 1}. Nº ${hymn.numero ?: "S/N"} - ${hymn.title?.trim() ?: "Sin título"}", margin, y, paint)
        y += 24f
        
        paint.textSize = 12f
        paint.typeface = android.graphics.Typeface.DEFAULT
        
        val lyrics = hymn.lyrics?.trim() ?: "Sin letra"
        val lines = lyrics.split('\n')
        for (line in lines) {
            if (y > pageHeight - 60f) {
                pdfDocument.finishPage(page)
                pageNum++
                pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 60f
            }
            canvas.drawText(line, margin, y, paint)
            y += 18f
        }
        y += 20f
    }
    
    pdfDocument.finishPage(page)
    
    val safeName = list.name.replace(Regex("[^\\p{L}\\p{N}\\s]"), "").take(50)
    val fileName = "$safeName.pdf"
    val file = java.io.File(context.cacheDir, fileName)
    try {
        pdfDocument.writeTo(java.io.FileOutputStream(file))
        pdfDocument.close()
        
        val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartir lista (PDF)"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// ==================== FUNCIONES PARA MODO MÚSICOS ====================

/**
 * Compartir himno con acordes transpuestos (Modo Músicos)
 */
fun shareHymnWithChords(
    context: Context, 
    hymn: HymnEntity, 
    transposedChords: String,
    tonality: String,
    isOriginal: Boolean
) {
    val sb = StringBuilder()
    sb.append("Nº ${hymn.numero ?: "S/N"}\n")
    sb.append("Título: ${hymn.title?.trim() ?: "Sin título"}\n")
    sb.append("Tonalidad: $tonality")
    if (!isOriginal) {
        sb.append(" (Transpuesta)")
    } else {
        sb.append(" (Original)")
    }
    sb.append("\n")
    sb.append("================\n\n")
    
    // Acordes transpuestos
    if (transposedChords.isNotBlank()) {
        sb.append("ACORDES:\n")
        sb.append(transposedChords)
        sb.append("\n\n")
    }
    
    sb.append("LETRA:\n")
    sb.append(hymn.lyrics?.trim() ?: "Sin letra")
    
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Compartir himno con acordes")
    context.startActivity(shareIntent)
}

/**
 * Exportar himno como PDF con acordes transpuestos (Modo Músicos)
 */
fun exportHymnWithChordsAsPdf(
    context: Context, 
    hymn: HymnEntity,
    transposedChords: String,
    tonality: String,
    isOriginal: Boolean
) {
    val pdfDocument = android.graphics.pdf.PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas
    val paint = android.graphics.Paint()
    val margin = 40f
    var y = 60f
    
    // Título del himno
    paint.textSize = 18f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    canvas.drawText("Nº ${hymn.numero ?: "S/N"}", margin, y, paint)
    y += 30f
    
    paint.textSize = 16f
    val title = hymn.title?.trim() ?: "Sin título"
    canvas.drawText("Título: $title", margin, y, paint)
    y += 30f
    
    // Tonalidad
    paint.textSize = 14f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD_ITALIC)
    val tonalityText = if (isOriginal) {
        "Tonalidad: $tonality (Original)"
    } else {
        "Tonalidad: $tonality (Transpuesta)"
    }
    canvas.drawText(tonalityText, margin, y, paint)
    y += 25f
    
    // Separador
    paint.strokeWidth = 2f
    canvas.drawLine(margin, y, pageWidth - margin, y, paint)
    y += 20f
    
    // Acordes transpuestos
    if (transposedChords.isNotBlank()) {
        paint.textSize = 12f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("ACORDES:", margin, y, paint)
        y += 20f
        
        paint.textSize = 11f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.NORMAL)
        
        val chordLines = transposedChords.split('\n')
        var pageNum = 1
        
        for (line in chordLines) {
            if (y > pageHeight - 60f) {
                pdfDocument.finishPage(page)
                pageNum++
                val newPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                page = pdfDocument.startPage(newPageInfo)
                canvas = page.canvas
                y = 60f
            }
            canvas.drawText(line, margin, y, paint)
            y += 18f
        }
        
        y += 15f
    }
    
    // Letra del himno
    paint.textSize = 12f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    
    if (y > pageHeight - 80f) {
        pdfDocument.finishPage(page)
        val newPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
        page = pdfDocument.startPage(newPageInfo)
        canvas = page.canvas
        y = 60f
    }
    
    canvas.drawText("LETRA:", margin, y, paint)
    y += 20f
    
    paint.textSize = 14f
    paint.typeface = android.graphics.Typeface.DEFAULT
    
    val lyrics = hymn.lyrics?.trim() ?: "Sin letra"
    val lines = lyrics.split('\n')
    var pageNum = if (y > 60f) 2 else 1
    
    for (line in lines) {
        if (y > pageHeight - 60f) {
            pdfDocument.finishPage(page)
            pageNum++
            val newPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            page = pdfDocument.startPage(newPageInfo)
            canvas = page.canvas
            y = 60f
        }
        canvas.drawText(line, margin, y, paint)
        y += 22f
    }
    
    pdfDocument.finishPage(page)
    
    val safeTitle = hymn.title?.trim()?.replace(Regex("[^\\p{L}\\p{N}\\s]"), "")?.take(50) ?: "Sin_titulo"
    val tonalitySafe = tonality.replace("/", "-")
    val fileName = "${hymn.numero ?: "sn"}. $safeTitle [$tonalitySafe].pdf"
    val file = java.io.File(context.cacheDir, fileName)
    try {
        pdfDocument.writeTo(java.io.FileOutputStream(file))
        pdfDocument.close()
        
        val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartir himno con acordes (PDF)"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

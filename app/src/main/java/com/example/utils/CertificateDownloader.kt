package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object CertificateDownloader {

    fun generateAndSaveCertificate(
        context: Context,
        userName: String,
        gridSize: Int,
        difficultyLabel: String,
        durationSeconds: Long,
        synapticSpeed: Double,
        focusRating: Double,
        globalPercentile: Double,
        aiEndorsement: String? = null,
        countryCode: String = "US",
        matchTitle: String = "MSB SUDOKU CHALLENGE"
    ): String? {
        val width = 1200
        val height = 900
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background (radial dark cosmic/gold gradient)
        val bgPaint = Paint().apply {
            isAntiAlias = true
        }
        val radialGradient = RadialGradient(
            width / 2f, height / 2f, height * 0.8f,
            Color.parseColor("#1c180d"), Color.parseColor("#020202"),
            Shader.TileMode.CLAMP
        )
        bgPaint.shader = radialGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        bgPaint.shader = null // clear shader

        val goldColor = Color.parseColor("#D4AF37")
        val whiteColor = Color.parseColor("#FFFFFF")
        val grayColor = Color.parseColor("#bebebe")
        val yellowColor = Color.parseColor("#FFD700")

        // Draw outer gold margin border
        val borderPaint = Paint().apply {
            color = goldColor
            style = Paint.Style.STROKE
            strokeWidth = 12f
            isAntiAlias = true
        }
        canvas.drawRect(20f, 20f, width - 20f, height - 20f, borderPaint)

        // Draw inner thin gold border
        borderPaint.strokeWidth = 3f
        canvas.drawRect(40f, 40f, width - 40f, height - 40f, borderPaint)

        // Draw corner triangle accents
        val cornerPaint = Paint().apply {
            color = goldColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        
        // Top Left Corner Ribbon
        val pathTL = Path().apply {
            moveTo(40f, 40f)
            lineTo(80f, 40f)
            lineTo(40f, 80f)
            close()
        }
        canvas.drawPath(pathTL, cornerPaint)

        // Top Right Corner Ribbon
        val pathTR = Path().apply {
            moveTo(width - 40f, 40f)
            lineTo(width - 80f, 40f)
            lineTo(width - 40f, 80f)
            close()
        }
        canvas.drawPath(pathTR, cornerPaint)

        // Bottom Left Corner Ribbon
        val pathBL = Path().apply {
            moveTo(40f, height - 40f)
            lineTo(80f, height - 40f)
            lineTo(40f, height - 80f)
            close()
        }
        canvas.drawPath(pathBL, cornerPaint)

        // Bottom Right Corner Ribbon
        val pathBR = Path().apply {
            moveTo(width - 40f, height - 40f)
            lineTo(width - 80f, height - 40f)
            lineTo(width - 40f, height - 80f)
            close()
        }
        canvas.drawPath(pathBR, cornerPaint)

        // 3. Text Paints
        val centerPaint = Paint().apply {
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Header Star Label
        centerPaint.color = goldColor
        centerPaint.textSize = 22f
        centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("★ ★ ★  COGNITIVE GRADUATED SOLVER  ★ ★ ★", width / 2f, 110f, centerPaint)

        // Title: Dynamic or Custom match title
        centerPaint.color = whiteColor
        centerPaint.textSize = 48f
        centerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(matchTitle.uppercase(Locale.getDefault()), width / 2f, 180f, centerPaint)

        // Sub-title
        centerPaint.color = goldColor
        centerPaint.textSize = 16f
        centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        canvas.drawText("OFFICIAL CERTIFICATE OF COGNITIVE GRADUATION", width / 2f, 220f, centerPaint)

        // Thin separator line
        val linePaint = Paint().apply {
            color = Color.argb(100, 212, 175, 55)
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawLine(width * 0.25f, 250f, width * 0.75f, 250f, linePaint)

        // Intro text
        centerPaint.color = grayColor
        centerPaint.textSize = 20f
        centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        canvas.drawText("This prestigious cognitive credential is formally awarded to", width / 2f, 300f, centerPaint)

        // User Name (Large yellow serif)
        centerPaint.color = yellowColor
        centerPaint.textSize = 45f
        centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        val displayName = if (userName.isBlank()) "MSB COGNITIVE SOLVER" else userName.uppercase(Locale.getDefault())
        canvas.drawText(displayName, width / 2f, 375f, centerPaint)

        // Subscript description
        centerPaint.color = Color.parseColor("#9e9e9e")
        centerPaint.textSize = 15f
        centerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("for exceptional logical precision, matrix resolution speed, and cognitive excellence", width / 2f, 420f, centerPaint)
        canvas.drawText("within the boundaries of MSB Creative Studios tournament specifications.", width / 2f, 442f, centerPaint)

        // Draw Cognitive AI Endorsement Seal
        if (!aiEndorsement.isNullOrBlank()) {
            val aiLabelPaint = Paint().apply {
                color = Color.parseColor("#4DE8F4") // Elegant AI Ice Blue
                textSize = 13f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val aiEndorsePaint = Paint().apply {
                color = Color.parseColor("#FFFFFF")
                textSize = 14f
                typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("⚡ INTEGRATED GOOGLE AI COGNITIVE SWEEP (HIGH PRECISION):", width / 2f, 475f, aiLabelPaint)
            canvas.drawText(aiEndorsement, width / 2f, 498f, aiEndorsePaint)
        } else {
            val aiLabelPaint = Paint().apply {
                color = Color.parseColor("#444444")
                textSize = 12f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("⚡ INTEGRATED GOOGLE AI COGNITIVE SWEEP PENDING", width / 2f, 485f, aiLabelPaint)
        }

        // 4. Specs Panel Rect (draw a clean boxed region, shifted down slightly)
        val rectPaint = Paint().apply {
            color = Color.argb(10, 255, 255, 255)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val panelLeft = width * 0.08f
        val panelRight = width * 0.92f
        val panelTop = 525f
        val panelBottom = 720f
        canvas.drawRect(panelLeft, panelTop, panelRight, panelBottom, rectPaint)

        val strokePaint = Paint().apply {
            color = Color.argb(60, 212, 175, 55)
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawRect(panelLeft, panelTop, panelRight, panelBottom, strokePaint)

        // Draw Specs Content
        val labelPaint = Paint().apply {
            color = goldColor
            textSize = 15f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }

        val valPaint = Paint().apply {
            color = whiteColor
            textSize = 15f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            isAntiAlias = true
        }

        // Columns layout inside panel
        val col1LabelX = panelLeft + 40f
        val col1ValueX = col1LabelX + 220f
        val col2LabelX = width / 2f + 20f
        val col2ValueX = col2LabelX + 240f

        // Row 1 (y = 560f)
        canvas.drawText("MATRIX SIZE:", col1LabelX, 560f, labelPaint)
        val matrixLabel = when (gridSize) {
            4 -> "4x4 Grid (Children Category)"
            9 -> "9x9 Grid (Standard Area Category)"
            else -> "${gridSize}x${gridSize} Grid"
        }
        canvas.drawText(matrixLabel, col1ValueX, 560f, valPaint)

        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        canvas.drawText("RECORD SOLVE TIME:", col2LabelX, 560f, labelPaint)
        canvas.drawText("$timeStr Duration", col2ValueX, 560f, valPaint)

        // Row 2 (y = 600f)
        canvas.drawText("DIFFICULTY:", col1LabelX, 600f, labelPaint)
        canvas.drawText(difficultyLabel.uppercase(Locale.getDefault()), col1ValueX, 600f, valPaint)

        val computedScore = maxOf(350, (synapticSpeed * 45 + (100 - globalPercentile) * 30 + (gridSize * 150) - (durationSeconds * 0.1)).toInt())
        canvas.drawText("FINAL GAME SCORE:", col2LabelX, 600f, labelPaint)
        
        val yellowTextPaint = Paint(valPaint).apply { color = yellowColor; typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD) }
        canvas.drawText("$computedScore PGP", col2ValueX, 600f, yellowTextPaint)

        // Row 3 (y = 640f)
        canvas.drawText("SYNAPTIC SPEED:", col1LabelX, 640f, labelPaint)
        canvas.drawText(String.format(Locale.getDefault(), "%.2f Hz", synapticSpeed), col1ValueX, 640f, valPaint)

        canvas.drawText("GLOBAL RANK:", col2LabelX, 640f, labelPaint)
        val orangeTextPaint = Paint(valPaint).apply { color = Color.parseColor("#FF9800"); typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD) }
        canvas.drawText(String.format(Locale.getDefault(), "TOP %.3f%%", globalPercentile), col2ValueX, 640f, orangeTextPaint)

        // Row 4 (y = 680f)
        val calendar = java.util.Calendar.getInstance()
        val dayStr = String.format("%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH))
        val monthStr = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault())?.uppercase() ?: "JUNE"
        val yearStr = calendar.get(java.util.Calendar.YEAR).toString()

        canvas.drawText("ACHIEVED DATE:", col1LabelX, 680f, labelPaint)
        canvas.drawText("$dayStr $monthStr $yearStr", col1ValueX, 680f, valPaint)

        canvas.drawText("COUNTRY CODE:", col2LabelX, 680f, labelPaint)
        canvas.drawText(countryCode, col2ValueX, 680f, valPaint)

        // Horizontal separator line at the bottom
        canvas.drawLine(panelLeft, 735f, panelRight, 735f, linePaint)

        // Credential signature hash
        val randHash = (System.currentTimeMillis() % 899999 + 100000).toInt()
        centerPaint.color = Color.parseColor("#555555")
        centerPaint.textSize = 13f
        centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        canvas.drawText("VERIFIED LEDGER CREDENTIAL HASH ID: MSB-$randHash", width / 2f, 765f, centerPaint)

        // Powered by MSB Creative Studios with highlight header
        centerPaint.color = yellowColor
        centerPaint.textSize = 20f
        centerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("HIGHLIGHT MSB SUDOKU CHALLENGE • POWERED BY MSB CREATIVE STUDIOS", width / 2f, 795f, centerPaint)

        // Subtitle footer
        centerPaint.color = Color.parseColor("#666666")
        centerPaint.textSize = 13f
        centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        canvas.drawText("Digital Certificate Generated via Client-Side Vector Image Pipeline", width / 2f, 825f, centerPaint)

        // 5. Save operation
        val cleanUserName = userName.ifBlank { "Solver" }.replace(" ", "_").trim().replace(Regex("[^a-zA-Z0-9_]"), "")
        val fileName = "MSB_Sudoku_Graduation_Certificate_${cleanUserName}.png"
        val contentResolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            try {
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.flush()
                stream.close()
                android.media.MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
                return "Downloads/" + file.name
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        if (uri != null) {
            try {
                contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
                return "Downloads/$fileName"
            } catch (e: Exception) {
                e.printStackTrace()
                contentResolver.delete(uri, null, null)
            }
        }
        return null
    }

    fun generateAndSavePdfCertificate(
        context: Context,
        userName: String,
        gridSize: Int,
        difficultyLabel: String,
        durationSeconds: Long,
        synapticSpeed: Double,
        focusRating: Double,
        globalPercentile: Double,
        aiEndorsement: String? = null,
        countryCode: String = "US",
        matchTitle: String = "MSB SUDOKU CHALLENGE"
    ): String? {
        val width = 1200
        val height = 900
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // 1. Draw Background (radial dark cosmic/gold gradient)
        val bgPaint = Paint().apply {
            isAntiAlias = true
        }
        val radialGradient = RadialGradient(
            width / 2f, height / 2f, height * 0.8f,
            Color.parseColor("#1c180d"), Color.parseColor("#020202"),
            Shader.TileMode.CLAMP
        )
        bgPaint.shader = radialGradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        bgPaint.shader = null // clear shader

        val goldColor = Color.parseColor("#D4AF37")
        val whiteColor = Color.parseColor("#FFFFFF")
        val grayColor = Color.parseColor("#bebebe")
        val yellowColor = Color.parseColor("#FFD700")

        // Draw outer gold margin border
        val borderPaint = Paint().apply {
            color = goldColor
            style = Paint.Style.STROKE
            strokeWidth = 12f
            isAntiAlias = true
        }
        canvas.drawRect(20f, 20f, width - 20f, height - 20f, borderPaint)

        // Draw inner thin gold border
        borderPaint.strokeWidth = 3f
        canvas.drawRect(40f, 40f, width - 40f, height - 40f, borderPaint)

        // Draw corner triangle accents
        val cornerPaint = Paint().apply {
            color = goldColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        
        // Top Left Corner
        val pathTL = Path().apply {
            moveTo(40f, 40f)
            lineTo(80f, 40f)
            lineTo(40f, 80f)
            close()
        }
        canvas.drawPath(pathTL, cornerPaint)

        // Top Right Corner
        val pathTR = Path().apply {
            moveTo(width - 40f, 40f)
            lineTo(width - 80f, 40f)
            lineTo(width - 40f, 80f)
            close()
        }
        canvas.drawPath(pathTR, cornerPaint)

        // Bottom Left Corner
        val pathBL = Path().apply {
            moveTo(40f, height - 40f)
            lineTo(80f, height - 40f)
            lineTo(40f, height - 80f)
            close()
        }
        canvas.drawPath(pathBL, cornerPaint)

        // Bottom Right Corner
        val pathBR = Path().apply {
            moveTo(width - 40f, height - 40f)
            lineTo(width - 80f, height - 40f)
            lineTo(width - 40f, height - 80f)
            close()
        }
        canvas.drawPath(pathBR, cornerPaint)

        // 3. Text Paints
        val centerPaint = Paint().apply {
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Header Star Label
        centerPaint.color = goldColor
        centerPaint.textSize = 22f
        centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("★ ★ ★  COGNITIVE GRADUATED SOLVER  ★ ★ ★", width / 2f, 110f, centerPaint)

        // Title: Dynamic or Custom match title
        centerPaint.color = whiteColor
        centerPaint.textSize = 48f
        centerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(matchTitle.uppercase(Locale.getDefault()), width / 2f, 180f, centerPaint)

        // Sub-title
        centerPaint.color = goldColor
        centerPaint.textSize = 16f
        centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        canvas.drawText("OFFICIAL CERTIFICATE OF COGNITIVE GRADUATION", width / 2f, 220f, centerPaint)

        // Thin separator line
        val linePaint = Paint().apply {
            color = Color.argb(100, 212, 175, 55)
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawLine(width * 0.25f, 250f, width * 0.75f, 250f, linePaint)

        // Intro text
        centerPaint.color = grayColor
        centerPaint.textSize = 20f
        centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        canvas.drawText("This prestigious cognitive credential is formally awarded to", width / 2f, 300f, centerPaint)

        // User Name
        centerPaint.color = yellowColor
        centerPaint.textSize = 45f
        centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        val displayName = if (userName.isBlank()) "MSB COGNITIVE SOLVER" else userName.uppercase(Locale.getDefault())
        canvas.drawText(displayName, width / 2f, 375f, centerPaint)

        // Description
        centerPaint.color = Color.parseColor("#9e9e9e")
        centerPaint.textSize = 15f
        centerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("for exceptional logical precision, matrix resolution speed, and cognitive excellence", width / 2f, 420f, centerPaint)
        canvas.drawText("within the boundaries of MSB Creative Studios tournament specifications.", width / 2f, 442f, centerPaint)

        // Draw Cognitive AI Endorsement Seal in PDF Canvas
        if (!aiEndorsement.isNullOrBlank()) {
            val aiLabelPaint = Paint().apply {
                color = Color.parseColor("#4DE8F4") // Elegant AI Ice Blue
                textSize = 13f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val aiEndorsePaint = Paint().apply {
                color = Color.parseColor("#FFFFFF")
                textSize = 14f
                typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("⚡ INTEGRATED GOOGLE AI COGNITIVE SWEEP (HIGH PRECISION):", width / 2f, 475f, aiLabelPaint)
            canvas.drawText(aiEndorsement, width / 2f, 498f, aiEndorsePaint)
        } else {
            val aiLabelPaint = Paint().apply {
                color = Color.parseColor("#444444")
                textSize = 12f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("⚡ INTEGRATED GOOGLE AI COGNITIVE SWEEP PENDING", width / 2f, 485f, aiLabelPaint)
        }

        // Spec rectangular background (Shifted down slightly)
        val rectPaint = Paint().apply {
            color = Color.argb(10, 255, 255, 255)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val panelLeft = width * 0.08f
        val panelRight = width * 0.92f
        val panelTop = 525f
        val panelBottom = 720f
        canvas.drawRect(panelLeft, panelTop, panelRight, panelBottom, rectPaint)

        val strokePaint = Paint().apply {
            color = Color.argb(60, 212, 175, 55)
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawRect(panelLeft, panelTop, panelRight, panelBottom, strokePaint)

        val labelPaint = Paint().apply {
            color = goldColor
            textSize = 15f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }

        val valPaint = Paint().apply {
            color = whiteColor
            textSize = 15f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            isAntiAlias = true
        }

        val col1LabelX = panelLeft + 40f
        val col1ValueX = col1LabelX + 220f
        val col2LabelX = width / 2f + 20f
        val col2ValueX = col2LabelX + 240f

        // Row 1 (y = 560f)
        canvas.drawText("MATRIX SIZE:", col1LabelX, 560f, labelPaint)
        val matrixLabel = when (gridSize) {
            4 -> "4x4 Grid (Children Category)"
            9 -> "9x9 Grid (Standard Area Category)"
            else -> "${gridSize}x${gridSize} Grid"
        }
        canvas.drawText(matrixLabel, col1ValueX, 560f, valPaint)

        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        canvas.drawText("RECORD SOLVE TIME:", col2LabelX, 560f, labelPaint)
        canvas.drawText("$timeStr Duration", col2ValueX, 560f, valPaint)

        // Row 2 (y = 600f)
        canvas.drawText("DIFFICULTY:", col1LabelX, 600f, labelPaint)
        canvas.drawText(difficultyLabel.uppercase(Locale.getDefault()), col1ValueX, 600f, valPaint)

        val computedScore = maxOf(350, (synapticSpeed * 45 + (100 - globalPercentile) * 30 + (gridSize * 150) - (durationSeconds * 0.1)).toInt())
        canvas.drawText("FINAL GAME SCORE:", col2LabelX, 600f, labelPaint)
        canvas.drawText("$computedScore PGP", col2ValueX, 600f, Paint(valPaint).apply { color = yellowColor; typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD) })

        // Row 3 (y = 640f)
        canvas.drawText("SYNAPTIC SPEED:", col1LabelX, 640f, labelPaint)
        canvas.drawText(String.format(Locale.getDefault(), "%.2f Hz", synapticSpeed), col1ValueX, 640f, valPaint)

        canvas.drawText("GLOBAL RANK:", col2LabelX, 640f, labelPaint)
        canvas.drawText(String.format(Locale.getDefault(), "TOP %.3f%%", globalPercentile), col2ValueX, 640f, Paint(valPaint).apply { color = Color.parseColor("#FF9800"); typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD) })

        // Row 4 (y = 680f)
        val calendar = java.util.Calendar.getInstance()
        val dayStr = String.format("%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH))
        val monthStr = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault())?.uppercase() ?: "JUNE"
        val yearStr = calendar.get(java.util.Calendar.YEAR).toString()

        canvas.drawText("ACHIEVED DATE:", col1LabelX, 680f, labelPaint)
        canvas.drawText("$dayStr $monthStr $yearStr", col1ValueX, 680f, valPaint)

        canvas.drawText("COUNTRY CODE:", col2LabelX, 680f, labelPaint)
        canvas.drawText(countryCode, col2ValueX, 680f, valPaint)

        canvas.drawLine(panelLeft, 735f, panelRight, 735f, linePaint)

        // Ledger Code
        val randHash = (System.currentTimeMillis() % 899999 + 100000).toInt()
        centerPaint.color = Color.parseColor("#555555")
        centerPaint.textSize = 13f
        centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        canvas.drawText("VERIFIED LEDGER CREDENTIAL HASH ID: MSB-$randHash", width / 2f, 765f, centerPaint)

        centerPaint.color = yellowColor
        centerPaint.textSize = 20f
        centerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("HIGHLIGHT MSB SUDOKU CHALLENGE • POWERED BY MSB CREATIVE STUDIOS", width / 2f, 792f, centerPaint)

        centerPaint.color = Color.parseColor("#666666")
        centerPaint.textSize = 13f
        centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        canvas.drawText("Digital Certificate Generated via Client-Side PDF Vector Pipeline", width / 2f, 825f, centerPaint)

        pdfDocument.finishPage(page)

        val cleanUserName = userName.ifBlank { "Solver" }.replace(" ", "_").trim().replace(Regex("[^a-zA-Z0-9_]"), "")
        val fileName = "MSB_Sudoku_Graduation_Certificate_${cleanUserName}.pdf"
        val contentResolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            try {
                val stream = FileOutputStream(file)
                pdfDocument.writeTo(stream)
                stream.flush()
                stream.close()
                pdfDocument.close()
                android.media.MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
                return "Downloads/" + file.name
            } catch (e: Exception) {
                e.printStackTrace()
                pdfDocument.close()
                return null
            }
        }

        if (uri != null) {
            try {
                contentResolver.openOutputStream(uri)?.use { out ->
                    pdfDocument.writeTo(out)
                }
                pdfDocument.close()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
                return "Downloads/$fileName"
            } catch (e: Exception) {
                e.printStackTrace()
                pdfDocument.close()
                contentResolver.delete(uri, null, null)
            }
        }
        return null
    }
}

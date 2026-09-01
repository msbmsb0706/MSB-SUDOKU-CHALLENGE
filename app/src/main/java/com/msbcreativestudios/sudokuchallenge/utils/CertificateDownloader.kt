package com.msbcreativestudios.sudokuchallenge.utils

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

    private fun drawCertificate(
        canvas: Canvas,
        width: Int,
        height: Int,
        userName: String,
        gridSize: Int,
        difficultyLabel: String,
        durationSeconds: Long,
        synapticSpeed: Double,
        focusRating: Double,
        globalPercentile: Double,
        aiEndorsement: String?,
        countryCode: String,
        matchTitle: String,
        designStyle: String
    ) {
        val style = designStyle.uppercase(Locale.getDefault())

        // 1. Draw Background
        val bgPaint = Paint().apply { isAntiAlias = true }
        when (style) {
            "NEON_CYBER" -> {
                val radialGradient = RadialGradient(
                    width / 2f, height / 2f, height * 0.8f,
                    Color.parseColor("#0d0f1a"), Color.parseColor("#040409"),
                    Shader.TileMode.CLAMP
                )
                bgPaint.shader = radialGradient
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
                bgPaint.shader = null

                // Cyber Grid
                val gridPaint = Paint().apply {
                    color = Color.argb(12, 0, 240, 255) // Cyan ultra-low opacity
                    strokeWidth = 1f
                    isAntiAlias = true
                }
                var y = 0f
                while (y < height) {
                    canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
                    y += 45f
                }
                var x = 0f
                while (x < width) {
                    canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
                    x += 45f
                }
            }
            "CLASSIC_IVORY" -> {
                val radialGradient = RadialGradient(
                    width / 2f, height / 2f, height * 0.8f,
                    Color.parseColor("#FAF6EE"), Color.parseColor("#EEDBBA"),
                    Shader.TileMode.CLAMP
                )
                bgPaint.shader = radialGradient
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
                bgPaint.shader = null
            }
            else -> { // COSMIC_LUXURY (Default)
                val radialGradient = RadialGradient(
                    width / 2f, height / 2f, height * 0.8f,
                    Color.parseColor("#1c180d"), Color.parseColor("#020202"),
                    Shader.TileMode.CLAMP
                )
                bgPaint.shader = radialGradient
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
                bgPaint.shader = null
            }
        }

        // Color Palette Configuration
        val goldColor = Color.parseColor("#D4AF37")
        val whiteColor = Color.parseColor("#FFFFFF")
        val grayColor = Color.parseColor("#bebebe")
        val yellowColor = Color.parseColor("#FFD700")
        val burgundyColor = Color.parseColor("#800020")
        val navyColor = Color.parseColor("#0D233A")
        val darkSlateColor = Color.parseColor("#222222")
        val neonCyanColor = Color.parseColor("#00F0FF")
        val neonMagentaColor = Color.parseColor("#FF007F")
        val neonGreenColor = Color.parseColor("#39FF14")

        // 2. Draw Borders and Corner Accents
        val borderPaint = Paint().apply {
            this.style = Paint.Style.STROKE
            isAntiAlias = true
        }

        when (style) {
            "NEON_CYBER" -> {
                borderPaint.color = neonMagentaColor
                borderPaint.strokeWidth = 12f
                canvas.drawRect(20f, 20f, width - 20f, height - 20f, borderPaint)

                borderPaint.color = neonCyanColor
                borderPaint.strokeWidth = 3f
                canvas.drawRect(40f, 40f, width - 40f, height - 40f, borderPaint)

                // Tech brackets at the corners
                val bracketPaint = Paint().apply {
                    color = neonCyanColor
                    strokeWidth = 6f
                    this.style = Paint.Style.STROKE
                    isAntiAlias = true
                }
                // top-left
                canvas.drawLine(40f, 40f, 110f, 40f, bracketPaint)
                canvas.drawLine(40f, 40f, 40f, 110f, bracketPaint)
                // top-right
                canvas.drawLine(width - 40f, 40f, width - 110f, 40f, bracketPaint)
                canvas.drawLine(width - 40f, 40f, width - 40f, 110f, bracketPaint)
                // bottom-left
                canvas.drawLine(40f, height - 40f, 110f, height - 40f, bracketPaint)
                canvas.drawLine(40f, height - 40f, 40f, height - 110f, bracketPaint)
                // bottom-right
                canvas.drawLine(width - 40f, height - 40f, width - 110f, height - 40f, bracketPaint)
                canvas.drawLine(width - 40f, height - 40f, width - 40f, height - 110f, bracketPaint)
            }
            "CLASSIC_IVORY" -> {
                borderPaint.color = burgundyColor
                borderPaint.strokeWidth = 12f
                canvas.drawRect(20f, 20f, width - 20f, height - 20f, borderPaint)

                borderPaint.color = navyColor
                borderPaint.strokeWidth = 3f
                canvas.drawRect(40f, 40f, width - 40f, height - 40f, borderPaint)

                // Elegant corner emblems
                val cornerRadius = 16f
                val cornerPaint = Paint().apply {
                    color = navyColor
                    isAntiAlias = true
                    this.style = Paint.Style.FILL
                }
                canvas.drawCircle(55f, 55f, cornerRadius, cornerPaint)
                canvas.drawCircle(width - 55f, 55f, cornerRadius, cornerPaint)
                canvas.drawCircle(55f, height - 55f, cornerRadius, cornerPaint)
                canvas.drawCircle(width - 55f, height - 55f, cornerRadius, cornerPaint)

                val starPaint = Paint().apply {
                    color = goldColor
                    textSize = 18f
                    typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                val starOffset = 6f
                canvas.drawText("★", 55f, 55f + starOffset, starPaint)
                canvas.drawText("★", width - 55f, 55f + starOffset, starPaint)
                canvas.drawText("★", 55f, height - 55f + starOffset, starPaint)
                canvas.drawText("★", width - 55f, height - 55f + starOffset, starPaint)
            }
            else -> { // COSMIC_LUXURY
                borderPaint.color = goldColor
                borderPaint.strokeWidth = 12f
                canvas.drawRect(20f, 20f, width - 20f, height - 20f, borderPaint)

                borderPaint.color = goldColor
                borderPaint.strokeWidth = 3f
                canvas.drawRect(40f, 40f, width - 40f, height - 40f, borderPaint)

                // Solid gold corner ribbons
                val ribbonPaint = Paint().apply {
                    color = goldColor
                    this.style = Paint.Style.FILL
                    isAntiAlias = true
                }
                // TL
                val pathTL = Path().apply {
                    moveTo(40f, 40f)
                    lineTo(80f, 40f)
                    lineTo(40f, 80f)
                    close()
                }
                canvas.drawPath(pathTL, ribbonPaint)
                // TR
                val pathTR = Path().apply {
                    moveTo(width - 40f, 40f)
                    lineTo(width - 80f, 40f)
                    lineTo(width - 40f, 80f)
                    close()
                }
                canvas.drawPath(pathTR, ribbonPaint)
                // BL
                val pathBL = Path().apply {
                    moveTo(40f, height - 40f)
                    lineTo(80f, height - 40f)
                    lineTo(40f, height - 80f)
                    close()
                }
                canvas.drawPath(pathBL, ribbonPaint)
                // BR
                val pathBR = Path().apply {
                    moveTo(width - 40f, height - 40f)
                    lineTo(width - 80f, height - 40f)
                    lineTo(width - 40f, height - 80f)
                    close()
                }
                canvas.drawPath(pathBR, ribbonPaint)
            }
        }

        // 3. Render Certificate Text Content
        val centerPaint = Paint().apply {
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // --- HEADER LABEL (y = 110f) ---
        when (style) {
            "NEON_CYBER" -> {
                centerPaint.color = neonGreenColor
                centerPaint.textSize = 20f
                centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                canvas.drawText("[ COGNITIVE SOLVER MATRIX SYSTEM OVERRIDE ]", width / 2f, 110f, centerPaint)
            }
            "CLASSIC_IVORY" -> {
                centerPaint.color = navyColor
                centerPaint.textSize = 21f
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                canvas.drawText("★  COGNITIO ET RESOLUTIO LUX VESTRA  ★", width / 2f, 110f, centerPaint)
            }
            else -> {
                centerPaint.color = goldColor
                centerPaint.textSize = 22f
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                canvas.drawText("★ ★ ★  COGNITIVE GRADUATED SOLVER  ★ ★ ★", width / 2f, 110f, centerPaint)
            }
        }

        // --- TITLE HEADER (y = 180f) ---
        val formattedTitle = matchTitle.uppercase(Locale.getDefault())
        when (style) {
            "NEON_CYBER" -> {
                centerPaint.color = neonCyanColor
                centerPaint.textSize = 42f
                centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                canvas.drawText("// CONG. $formattedTitle", width / 2f, 180f, centerPaint)
            }
            "CLASSIC_IVORY" -> {
                centerPaint.color = burgundyColor
                centerPaint.textSize = 50f
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                canvas.drawText(formattedTitle, width / 2f, 180f, centerPaint)
            }
            else -> {
                centerPaint.color = whiteColor
                centerPaint.textSize = 48f
                centerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(formattedTitle, width / 2f, 180f, centerPaint)
            }
        }

        // --- SUBTITLE (y = 220f) ---
        when (style) {
            "NEON_CYBER" -> {
                centerPaint.color = neonMagentaColor
                centerPaint.textSize = 15f
                centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                canvas.drawText("NODE_RECORD // LEVEL COGNITIVE CONFR_DEGREE_SECURE", width / 2f, 220f, centerPaint)
            }
            "CLASSIC_IVORY" -> {
                centerPaint.color = navyColor
                centerPaint.textSize = 16f
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                canvas.drawText("ACADEMIC TESTIMONIAL OF COGNITIVE LAUREATE", width / 2f, 220f, centerPaint)
            }
            else -> {
                centerPaint.color = goldColor
                centerPaint.textSize = 16f
                centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                canvas.drawText("OFFICIAL CERTIFICATE OF COGNITIVE GRADUATION", width / 2f, 220f, centerPaint)
            }
        }

        // --- SOLID THIN SEPARATOR LINE ---
        val linePaint = Paint().apply {
            strokeWidth = 2f
            isAntiAlias = true
        }
        when (style) {
            "NEON_CYBER" -> linePaint.color = Color.argb(120, 0, 240, 255)
            "CLASSIC_IVORY" -> linePaint.color = Color.argb(100, 128, 0, 32)
            else -> linePaint.color = Color.argb(100, 212, 175, 55)
        }
        canvas.drawLine(width * 0.25f, 250f, width * 0.75f, 250f, linePaint)

        // --- INTRO LEVEL TEXT (y = 300f) ---
        centerPaint.textSize = 20f
        when (style) {
            "NEON_CYBER" -> {
                centerPaint.color = grayColor
                centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                canvas.drawText(">> This terminal hereby registers logical confirmation for:", width / 2f, 300f, centerPaint)
            }
            "CLASSIC_IVORY" -> {
                centerPaint.color = darkSlateColor
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                canvas.drawText("This solemn academic credential is formally awarded and recognized to", width / 2f, 300f, centerPaint)
            }
            else -> {
                centerPaint.color = grayColor
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                canvas.drawText("This prestigious cognitive credential is formally awarded to", width / 2f, 300f, centerPaint)
            }
        }

        // --- HOLDER USER NAME (y = 375f) ---
        val displayName = if (userName.isBlank()) "MSB COGNITIVE SOLVER" else userName.uppercase(Locale.getDefault())
        centerPaint.textSize = 45f
        when (style) {
            "NEON_CYBER" -> {
                centerPaint.color = neonMagentaColor
                centerPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            }
            "CLASSIC_IVORY" -> {
                centerPaint.color = navyColor
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            }
            else -> {
                centerPaint.color = yellowColor
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            }
        }
        canvas.drawText(displayName, width / 2f, 375f, centerPaint)

        // --- DESCRIPTION TEXT (y = 420-442f) ---
        centerPaint.textSize = 15f
        centerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        when (style) {
            "NEON_CYBER" -> {
                centerPaint.color = neonCyanColor
                canvas.drawText("// CONTEXT: demonstrating flawless execution profiles, parsing complex multi-quadrant matrices", width / 2f, 420f, centerPaint)
                canvas.drawText("// and achieving extreme synaptic computation metrics in real-time execution tests.", width / 2f, 442f, centerPaint)
            }
            "CLASSIC_IVORY" -> {
                centerPaint.color = darkSlateColor
                centerPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
                canvas.drawText("for demonstrating exquisite logical precision, rapid matrix solving capacity, and elite performance", width / 2f, 420f, centerPaint)
                canvas.drawText("within the rigorous intellectual specifications established by MSB Academy.", width / 2f, 442f, centerPaint)
            }
            else -> {
                centerPaint.color = Color.parseColor("#9e9e9e")
                canvas.drawText("for exceptional logical precision, matrix resolution speed, and cognitive excellence", width / 2f, 420f, centerPaint)
                canvas.drawText("within the boundaries of MSB Creative Studios tournament specifications.", width / 2f, 442f, centerPaint)
            }
        }

        // --- AI ENDORSEMENT STATEMENT (y = 475-498f) ---
        if (!aiEndorsement.isNullOrBlank()) {
            val aiLabelPaint = Paint().apply {
                textSize = 13f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val aiEndorsePaint = Paint().apply {
                textSize = 14f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            when (style) {
                "NEON_CYBER" -> {
                    aiLabelPaint.color = neonGreenColor
                    aiLabelPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    aiEndorsePaint.color = neonGreenColor
                    aiEndorsePaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)

                    canvas.drawText("<< COGNITIVE AI LOGICAL CRITIQUE VERDICT_SECURE >>", width / 2f, 475f, aiLabelPaint)
                    canvas.drawText("\"$aiEndorsement\"", width / 2f, 498f, aiEndorsePaint)
                }
                "CLASSIC_IVORY" -> {
                    aiLabelPaint.color = burgundyColor
                    aiLabelPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                    aiEndorsePaint.color = darkSlateColor
                    aiEndorsePaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)

                    canvas.drawText("✒️ ACADEMIC COGNITIVE AI ENDORSEMENT VERIFY:", width / 2f, 475f, aiLabelPaint)
                    canvas.drawText("\"$aiEndorsement\"", width / 2f, 498f, aiEndorsePaint)
                }
                else -> {
                    aiLabelPaint.color = Color.parseColor("#4DE8F4") // AI Ice Blue
                    aiLabelPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    aiEndorsePaint.color = whiteColor
                    aiEndorsePaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)

                    canvas.drawText("⚡ INTEGRATED COGNITIVE AI CRITIQUE (HIGH PRECISION):", width / 2f, 475f, aiLabelPaint)
                    canvas.drawText(aiEndorsement, width / 2f, 498f, aiEndorsePaint)
                }
            }
        } else {
            val pendingPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            when (style) {
                "NEON_CYBER" -> {
                    pendingPaint.color = Color.argb(150, 0, 240, 255)
                    canvas.drawText("// COGNITIVE AI CO-INTEGRATION STACK: IDLE_STANDBY", width / 2f, 485f, pendingPaint)
                }
                "CLASSIC_IVORY" -> {
                    pendingPaint.color = burgundyColor
                    canvas.drawText("✒️ COGNITIVE AI AUDIT SEAL: PENDING MANUSCRIPT CONFIRMATION", width / 2f, 485f, pendingPaint)
                }
                else -> {
                    pendingPaint.color = Color.parseColor("#444444")
                    canvas.drawText("⚡ INTEGRATED COGNITIVE AI CRITIQUE PENDING", width / 2f, 485f, pendingPaint)
                }
            }
        }

        // --- SPECS METRIC PANEL (y = 525 to 720f) ---
        val rectPaint = Paint().apply {
            this.style = Paint.Style.FILL
            isAntiAlias = true
        }
        val strokePaint = Paint().apply {
            this.style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }

        when (style) {
            "NEON_CYBER" -> {
                rectPaint.color = Color.argb(30, 0, 240, 255) // Cyber filled transparent cyan
                strokePaint.color = neonCyanColor
            }
            "CLASSIC_IVORY" -> {
                rectPaint.color = Color.argb(15, 128, 0, 32) // Warm transparent burgundy
                strokePaint.color = Color.argb(100, 13, 35, 58) // Navy outline
            }
            else -> {
                rectPaint.color = Color.argb(10, 255, 255, 255)
                strokePaint.color = Color.argb(60, 212, 175, 55)
            }
        }

        val panelLeft = width * 0.08f
        val panelRight = width * 0.92f
        val panelTop = 525f
        val panelBottom = 720f
        canvas.drawRect(panelLeft, panelTop, panelRight, panelBottom, rectPaint)
        canvas.drawRect(panelLeft, panelTop, panelRight, panelBottom, strokePaint)

        // Metrics text layout configuration
        val labelPaint = Paint().apply {
            textSize = 15f
            isAntiAlias = true
        }
        val valPaint = Paint().apply {
            textSize = 15f
            isAntiAlias = true
        }

        when (style) {
            "NEON_CYBER" -> {
                labelPaint.color = neonCyanColor
                labelPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                valPaint.color = whiteColor
                valPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            }
            "CLASSIC_IVORY" -> {
                labelPaint.color = burgundyColor
                labelPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                valPaint.color = darkSlateColor
                valPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            }
            else -> {
                labelPaint.color = goldColor
                labelPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                valPaint.color = whiteColor
                valPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            }
        }

        val col1LabelX = panelLeft + 40f
        val col1ValueX = col1LabelX + 220f
        val col2LabelX = width / 2f + 20f
        val col2ValueX = col2LabelX + 240f

        // Row 1 metrics (y = 560f)
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

        // Row 2 metrics (y = 600f)
        canvas.drawText("DIFFICULTY:", col1LabelX, 600f, labelPaint)
        canvas.drawText(difficultyLabel.uppercase(Locale.getDefault()), col1ValueX, 600f, valPaint)

        val computedScore = maxOf(350, (synapticSpeed * 45 + (100 - globalPercentile) * 30 + (gridSize * 150) - (durationSeconds * 0.1)).toInt())
        canvas.drawText("FINAL GAME SCORE:", col2LabelX, 600f, labelPaint)

        val scoreHighlightPaint = Paint(valPaint).apply {
            color = when (style) {
                "NEON_CYBER" -> neonGreenColor
                "CLASSIC_IVORY" -> burgundyColor
                else -> yellowColor
            }
            typeface = if (style == "CLASSIC_IVORY") Typeface.create(Typeface.SERIF, Typeface.BOLD) else Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
        canvas.drawText("$computedScore PGP", col2ValueX, 600f, scoreHighlightPaint)

        // Row 3 metrics (y = 640f)
        canvas.drawText("SYNAPTIC SPEED:", col1LabelX, 640f, labelPaint)
        canvas.drawText(String.format(Locale.getDefault(), "%.2f Hz", synapticSpeed), col1ValueX, 640f, valPaint)

        canvas.drawText("GLOBAL RANK:", col2LabelX, 640f, labelPaint)
        val rankHighlightPaint = Paint(valPaint).apply {
            color = when (style) {
                "NEON_CYBER" -> neonMagentaColor
                "CLASSIC_IVORY" -> navyColor
                else -> Color.parseColor("#FF9800")
            }
            typeface = if (style == "CLASSIC_IVORY") Typeface.create(Typeface.SERIF, Typeface.BOLD) else Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
        canvas.drawText(String.format(Locale.getDefault(), "TOP %.3f%%", globalPercentile), col2ValueX, 640f, rankHighlightPaint)

        // Row 4 metrics (y = 680f)
        val calendar = java.util.Calendar.getInstance()
        val dayStr = String.format(Locale.getDefault(), "%02d", calendar.get(java.util.Calendar.DAY_OF_MONTH))
        val monthStr = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault())?.uppercase(Locale.getDefault()) ?: "JUNE"
        val yearStr = calendar.get(java.util.Calendar.YEAR).toString()

        canvas.drawText("ACHIEVED DATE:", col1LabelX, 680f, labelPaint)
        canvas.drawText("$dayStr $monthStr $yearStr", col1ValueX, 680f, valPaint)

        canvas.drawText("COUNTRY CODE:", col2LabelX, 680f, labelPaint)
        canvas.drawText(countryCode, col2ValueX, 680f, valPaint)

        // Separator line below credentials (y=735f)
        canvas.drawLine(panelLeft, 735f, panelRight, 735f, linePaint)

        // --- FOOTERS (y = 765, 792, 825) ---
        val randHash = (System.currentTimeMillis() % 899999 + 100000).toInt()
        centerPaint.textSize = 13f
        centerPaint.typeface = if (style == "CLASSIC_IVORY") Typeface.create(Typeface.SERIF, Typeface.NORMAL) else Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        when (style) {
            "NEON_CYBER" -> centerPaint.color = Color.argb(180, 0, 240, 255)
            "CLASSIC_IVORY" -> centerPaint.color = Color.parseColor("#444444")
            else -> centerPaint.color = Color.parseColor("#555555")
        }
        canvas.drawText("VERIFIED SECURE COGNITIVE LEDGER INTEGRITY ID: MSB-$randHash", width / 2f, 765f, centerPaint)

        centerPaint.textSize = 20f
        centerPaint.typeface = if (style == "CLASSIC_IVORY") Typeface.create(Typeface.SERIF, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        when (style) {
            "NEON_CYBER" -> {
                centerPaint.color = neonMagentaColor
                canvas.drawText("[ CYBERNETIC COGNITIVE COMPETENCY NODE • MSB EXPERIMENTAL ]", width / 2f, 795f, centerPaint)
            }
            "CLASSIC_IVORY" -> {
                centerPaint.color = burgundyColor
                canvas.drawText("EX COGNITIONE TRIUMPHUS • POWERED BY MSB CREATIVE STUDIOS", width / 2f, 795f, centerPaint)
            }
            else -> {
                centerPaint.color = yellowColor
                canvas.drawText("HIGHLIGHT MSBC SUDOKU CHALLENGE • POWERED BY MSB CREATIVE STUDIOS", width / 2f, 795f, centerPaint)
            }
        }

        centerPaint.textSize = 13f
        centerPaint.typeface = if (style == "CLASSIC_IVORY") Typeface.create(Typeface.SERIF, Typeface.NORMAL) else Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        when (style) {
            "NEON_CYBER" -> centerPaint.color = Color.argb(120, 0, 240, 255)
            "CLASSIC_IVORY" -> centerPaint.color = Color.parseColor("#666666")
            else -> centerPaint.color = Color.parseColor("#666666")
        }
        val genModeText = if (style == "CLASSIC_IVORY") "Digital Certificate Generated via Client-Side Core Serif Vector Pipeline" 
                          else "Digital Certificate Generated via Client-Side Vector Image Pipeline"
        canvas.drawText(genModeText, width / 2f, 825f, centerPaint)
    }

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
        matchTitle: String = "MSB SUDOKU CHALLENGE",
        designStyle: String = "COSMIC_LUXURY"
    ): String? {
        val width = 1200
        val height = 900
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawCertificate(
            canvas = canvas,
            width = width,
            height = height,
            userName = userName,
            gridSize = gridSize,
            difficultyLabel = difficultyLabel,
            durationSeconds = durationSeconds,
            synapticSpeed = synapticSpeed,
            focusRating = focusRating,
            globalPercentile = globalPercentile,
            aiEndorsement = aiEndorsement,
            countryCode = countryCode,
            matchTitle = matchTitle,
            designStyle = designStyle
        )

        // Save Bitmap to MediaStore / Download folder
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
        matchTitle: String = "MSB SUDOKU CHALLENGE",
        designStyle: String = "COSMIC_LUXURY"
    ): String? {
        val width = 1200
        val height = 900
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawCertificate(
            canvas = canvas,
            width = width,
            height = height,
            userName = userName,
            gridSize = gridSize,
            difficultyLabel = difficultyLabel,
            durationSeconds = durationSeconds,
            synapticSpeed = synapticSpeed,
            focusRating = focusRating,
            globalPercentile = globalPercentile,
            aiEndorsement = aiEndorsement,
            countryCode = countryCode,
            matchTitle = matchTitle,
            designStyle = designStyle
        )

        pdfDocument.finishPage(page)

        // Save PDF to MediaStore / Download folder
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

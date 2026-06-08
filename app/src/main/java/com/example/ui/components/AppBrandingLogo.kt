package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppBrandingLogo(modifier: Modifier = Modifier) {
    val goldColor = Color(0xFFFFC107)
    val cyanColor = Color(0xFF00BCD4)
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f)
        )
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High fidelity vector-drawn digital board
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundBrush)
                .border(2.dp, goldColor, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                val sizePx = size.width
                val cellSize = sizePx / 3f

                // Draw neon background grid block
                drawRect(
                    color = cyanColor.copy(alpha = 0.08f),
                    size = Size(sizePx, sizePx)
                )

                // Grid divisions
                for (i in 1..2) {
                    val pos = i * cellSize
                    // Vertical borders
                    drawLine(
                        color = cyanColor.copy(alpha = 0.4f),
                        start = Offset(pos, 0f),
                        end = Offset(pos, sizePx),
                        strokeWidth = 2f
                    )
                    // Horizontal borders
                    drawLine(
                        color = cyanColor.copy(alpha = 0.4f),
                        start = Offset(0f, pos),
                        end = Offset(sizePx, pos),
                        strokeWidth = 2f
                    )
                }

                // Main outer frame inside canvas
                drawRect(
                    color = goldColor.copy(alpha = 0.7f),
                    size = Size(sizePx, sizePx),
                    style = Stroke(width = 4f)
                )
            }

            // Stylized Sudoku central text matrix characters overlaid on grid cells
            Column(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("5", color = goldColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text("M", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    Text("9", color = cyanColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("S", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    Text("🧠", fontSize = 14.sp)
                    Text("B", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("1", color = cyanColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text("7", color = goldColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    Text("3", color = goldColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "MSB SUDOKU CHALLENGE",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(1.dp)
                    .background(goldColor)
            )
            Text(
                text = " POWERED BY MSB CREATIVE STUDIOS ",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                color = foldToAccentColor(cyanColor, goldColor),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(1.dp)
                    .background(goldColor)
            )
        }
    }
}

@Composable
private fun foldToAccentColor(cyan: Color, gold: Color): Color {
    return if (MaterialTheme.colorScheme.primary == Color.White) gold else MaterialTheme.colorScheme.primary
}

package com.msbcreativestudios.sudokuchallenge.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msbcreativestudios.sudokuchallenge.ui.SudokuCell

@Composable
fun SudokuGrid(
    grid: List<SudokuCell>,
    selectedCell: Pair<Int, Int>?,
    onCellSelected: (Int, Int) -> Unit,
    isPaused: Boolean,
    disableGridHelpers: Boolean = false,
    hideLastRow: Boolean = false,
    modifier: Modifier = Modifier
) {
    val size = when (grid.size) {
        9 -> 3
        16 -> 4
        else -> 9
    }
    val boxWidth = when (size) {
        3 -> 3
        4 -> 2
        else -> 3
    }
    val boxHeight = when (size) {
        3 -> 3
        4 -> 2
        else -> 3
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val boardSize = if (maxHeight > 0.dp && maxHeight.value.isFinite()) {
            minOf(maxWidth, maxHeight)
        } else {
            maxWidth
        }

        Box(
            modifier = Modifier
                .size(boardSize)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            val cellSize = boardSize / size

            // Render base full grid lines or overlapping box layers safely
        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0 until size) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    for (c in 0 until size) {
                        val index = r * size + c
                        val cell = grid.getOrNull(index) ?: SudokuCell(r, c, 0, false)

                        // Highlight States
                        val isSelected = selectedCell != null && selectedCell.first == r && selectedCell.second == c
                        val hasSelectedFocus = selectedCell != null
                        val isSameRowOrCol = selectedCell != null && (selectedCell.first == r || selectedCell.second == c)
                        val isSameSquare = selectedCell != null && (selectedCell.first / boxHeight == r / boxHeight && selectedCell.second / boxWidth == c / boxWidth)

                        // If user has selected a cell, highlight cells with matching entered values of the same number
                        val selectedCellValue = selectedCell?.let { (sr, sc) -> grid.getOrNull(sr * size + sc)?.value } ?: 0
                        val isMatchingValue = selectedCellValue > 0 && cell.value == selectedCellValue

                        // Outlines & grid dividers optimized for high density and modern contrast
                        val borderThick = 4.dp
                        val borderThin = 1.5.dp

                        val borderTop = if (r % boxHeight == 0) borderThick else borderThin
                        val borderBottom = if (r == size - 1) borderThick else borderThin
                        val borderLeft = if (c % boxWidth == 0) borderThick else borderThin
                        val borderRight = if (c == size - 1) borderThick else borderThin

                        // Background Colors with optimal, highly visible alpha weights under different selected themes
                        val defaultBg = MaterialTheme.colorScheme.surface
                        val finalBg = when {
                            isPaused -> MaterialTheme.colorScheme.surfaceVariant
                            cell.isHint -> Color(0xFFE8F5E9) // Pastel green / mint for hint cells
                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f) // Vivid focus active cell
                            cell.isError -> Color(0xFFFFCDD2) // Crimson error highlight
                            !disableGridHelpers && isMatchingValue -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f) // Distinct highlight for identical digits
                            !disableGridHelpers && isSameRowOrCol -> MaterialTheme.colorScheme.primary.copy(alpha = 0.28f) // Highly visible themed lane crosshair highlight
                            !disableGridHelpers && isSameSquare -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f) // High-contrast square sub-box accent
                            else -> defaultBg
                        }

                        // Boundary outlines use full-strength Primary theme color, internal lines use visible structural grey
                        val boundaryColor = MaterialTheme.colorScheme.primary
                        val internalColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f)

                        val topColor = if (r % boxHeight == 0) boundaryColor else internalColor
                        val bottomColor = if (r == size - 1) boundaryColor else internalColor
                        val leftColor = if (c % boxWidth == 0) boundaryColor else internalColor
                        val rightColor = if (c == size - 1) boundaryColor else internalColor

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(finalBg)
                                .drawCustomBorders(
                                    top = borderTop,
                                    bottom = borderBottom,
                                    left = borderLeft,
                                    right = borderRight,
                                    topColor = topColor,
                                    bottomColor = bottomColor,
                                    leftColor = leftColor,
                                    rightColor = rightColor
                                )
                                .clickable { onCellSelected(r, c) }
                                .testTag("cell_${r}_${c}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isPaused) {
                                if (cell.value > 0) {
                                    val isHiddenDigit = hideLastRow && r == size - 1
                                    val textColor = when {
                                        cell.isHint -> Color(0xFF2E7D32) // Emerald Green for hint values
                                        cell.isClue -> MaterialTheme.colorScheme.onSurface
                                        cell.isError -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                    val fontWeight = if (cell.isClue) FontWeight.Bold else FontWeight.Medium

                                    Text(
                                        text = if (isHiddenDigit) "?" else cell.value.toString(),
                                        color = if (isHiddenDigit) Color(0xFFFF9800) else textColor,
                                        fontWeight = if (isHiddenDigit) FontWeight.Black else fontWeight,
                                        fontSize = if (size == 3) 28.sp else if (size == 4) 24.sp else 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                } else if (cell.pencilNotes.isNotEmpty()) {
                                    // Render adaptive pencil notes micro-grid
                                    PencilNotesGrid(notes = cell.pencilNotes, size = size)
                                }
                            } else {
                                // Paused lock symbol
                                if (r == size / 2 && c == size / 2) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Paused Icon",
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Overlay paused state shield
        AnimatedVisibility(
            visible = isPaused,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TAP RESUME TO REVEAL BOARD",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
        }
    }
}

@Composable
fun PencilNotesGrid(notes: Set<Int>, size: Int = 9) {
    if (size == 3) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (noteNum in 1..3) {
                val showNote = notes.contains(noteNum)
                Text(
                    text = if (showNote) noteNum.toString() else " ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }
        }
    } else if (size == 4) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (rowGroup in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (colGroup in 1..2) {
                        val noteNum = rowGroup * 2 + colGroup
                        val showNote = notes.contains(noteNum)
                        Text(
                            text = if (showNote) noteNum.toString() else " ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(11.dp)
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (rowGroup in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (colGroup in 1..3) {
                        val noteNum = rowGroup * 3 + colGroup
                        val showNote = notes.contains(noteNum)
                        Text(
                            text = if (showNote) noteNum.toString() else " ",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(9.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Border modifier helper to draw borders around cells symmetrically with high contrast.
 */
fun Modifier.drawCustomBorders(
    top: androidx.compose.ui.unit.Dp,
    bottom: androidx.compose.ui.unit.Dp,
    left: androidx.compose.ui.unit.Dp,
    right: androidx.compose.ui.unit.Dp,
    topColor: Color,
    bottomColor: Color,
    leftColor: Color,
    rightColor: Color
): Modifier {
    return this.drawDetailedBorders(top, bottom, left, right, topColor, bottomColor, leftColor, rightColor)
}

fun Modifier.drawDetailedBorders(
    top: androidx.compose.ui.unit.Dp,
    bottom: androidx.compose.ui.unit.Dp,
    left: androidx.compose.ui.unit.Dp,
    right: androidx.compose.ui.unit.Dp,
    topColor: Color,
    bottomColor: Color,
    leftColor: Color,
    rightColor: Color
): Modifier {
    return this.drawBehind {
        val strokeWidthTop = top.toPx()
        val strokeWidthBottom = bottom.toPx()
        val strokeWidthLeft = left.toPx()
        val strokeWidthRight = right.toPx()

        val width = size.width
        val height = size.height

        // Top line
        if (strokeWidthTop > 0) {
            drawLine(
                color = topColor,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(width, 0f),
                strokeWidth = strokeWidthTop
            )
        }
        // Left line
        if (strokeWidthLeft > 0) {
            drawLine(
                color = leftColor,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(0f, height),
                strokeWidth = strokeWidthLeft
            )
        }
        // Bottom line
        if (strokeWidthBottom > 0) {
            drawLine(
                color = bottomColor,
                start = androidx.compose.ui.geometry.Offset(0f, height),
                end = androidx.compose.ui.geometry.Offset(width, height),
                strokeWidth = strokeWidthBottom
            )
        }
        // Right line
        if (strokeWidthRight > 0) {
            drawLine(
                color = rightColor,
                start = androidx.compose.ui.geometry.Offset(width, 0f),
                end = androidx.compose.ui.geometry.Offset(width, height),
                strokeWidth = strokeWidthRight
            )
        }
    }
}



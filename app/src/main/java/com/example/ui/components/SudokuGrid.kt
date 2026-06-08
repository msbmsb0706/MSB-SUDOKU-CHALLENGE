package com.example.ui.components

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
import com.example.ui.SudokuCell

@Composable
fun SudokuGrid(
    grid: List<SudokuCell>,
    selectedCell: Pair<Int, Int>?,
    onCellSelected: (Int, Int) -> Unit,
    isPaused: Boolean,
    modifier: Modifier = Modifier
) {
    val size = if (grid.size == 16) 4 else 9
    val boxWidth = if (size == 4) 2 else 3
    val boxHeight = if (size == 4) 2 else 3

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        val cellSize = maxWidth / size

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

                        // Thick lines (subgrid markers) matching borders
                        val borderTop = if (r % boxHeight == 0) 2.5.dp else 0.5.dp
                        val borderBottom = if (r == size - 1) 2.5.dp else 0.5.dp
                        val borderLeft = if (c % boxWidth == 0) 2.5.dp else 0.5.dp
                        val borderRight = if (c == size - 1) 2.5.dp else 0.5.dp

                        // Background Colors based on M3 theme
                        val defaultBg = MaterialTheme.colorScheme.surface
                        val finalBg = when {
                            isPaused -> MaterialTheme.colorScheme.surfaceVariant
                            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            cell.isError -> Color(0xFFFDE8E8) // Visual red alert highlight for mistaken entries!
                            isMatchingValue -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                            isSameRowOrCol || isSameSquare -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            else -> defaultBg
                        }

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
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = if (r % boxHeight == 0 || c % boxWidth == 0 || r == size - 1 || c == size - 1) 0.5f else 0.15f
                                    )
                                )
                                .clickable { onCellSelected(r, c) }
                                .testTag("cell_${r}_${c}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isPaused) {
                                if (cell.value > 0) {
                                    val textColor = when {
                                        cell.isClue -> MaterialTheme.colorScheme.onSurface
                                        cell.isError -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                    val fontWeight = if (cell.isClue) FontWeight.Bold else FontWeight.Medium

                                    Text(
                                        text = cell.value.toString(),
                                        color = textColor,
                                        fontWeight = fontWeight,
                                        fontSize = if (size == 4) 24.sp else 20.sp,
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

@Composable
fun PencilNotesGrid(notes: Set<Int>, size: Int = 9) {
    if (size == 4) {
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
 * Custom Border modifier helper to draw borders around cells symmetrically.
 */
fun Modifier.drawCustomBorders(
    top: androidx.compose.ui.unit.Dp,
    bottom: androidx.compose.ui.unit.Dp,
    left: androidx.compose.ui.unit.Dp,
    right: androidx.compose.ui.unit.Dp,
    color: Color
): Modifier {
    return this.drawBehindBorder(top, bottom, left, right, color)
}

fun Modifier.drawBehindBorder(
    top: androidx.compose.ui.unit.Dp,
    bottom: androidx.compose.ui.unit.Dp,
    left: androidx.compose.ui.unit.Dp,
    right: androidx.compose.ui.unit.Dp,
    color: Color
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
                color = color,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(width, 0f),
                strokeWidth = strokeWidthTop
            )
        }
        // Left line
        if (strokeWidthLeft > 0) {
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(0f, height),
                strokeWidth = strokeWidthLeft
            )
        }
        // Bottom line
        if (strokeWidthBottom > 0) {
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(0f, height),
                end = androidx.compose.ui.geometry.Offset(width, height),
                strokeWidth = strokeWidthBottom
            )
        }
        // Right line
        if (strokeWidthRight > 0) {
            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(width, 0f),
                end = androidx.compose.ui.geometry.Offset(width, height),
                strokeWidth = strokeWidthRight
            )
        }
    }
}



package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ControlPanel(
    pencilMode: Boolean,
    onPencilToggle: () -> Unit,
    onClear: () -> Unit,
    onHint: () -> Unit,
    onNumberEntered: (Int) -> Unit,
    gemsRemaining: Int,
    gridSize: Int = 9,
    disableGridHelpers: Boolean = false,
    onToggleGridHelpers: (() -> Unit)? = null,
    hideLastRow: Boolean = false,
    onToggleHideLastRow: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Core tools: Pencil Toggle, Erase, Hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pencil Toggle Button
            ToolButton(
                icon = Icons.Default.Create,
                label = "Notes",
                isActive = pencilMode,
                onClick = onPencilToggle,
                badge = if (pencilMode) "ON" else null,
                testTag = "pencil_tool_btn",
                modifier = Modifier.weight(1f)
            )

            // Erase / Clear Button
            ToolButton(
                icon = Icons.Default.Clear,
                label = "Erase",
                isActive = false,
                onClick = onClear,
                badge = null,
                testTag = "erase_tool_btn",
                modifier = Modifier.weight(1f)
            )

            // Hint Button
            ToolButton(
                icon = Icons.Default.Info,
                label = "Hint",
                isActive = false,
                onClick = onHint,
                badge = "${gemsRemaining}G", // displays remaining gems
                testTag = "hint_tool_btn",
                modifier = Modifier.weight(1f)
            )
        }

        // 1. Sleek, intuitive hand-gesture drag & swipe helper text banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Swipe Tip",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "👉 Drag / Swipe horizontal bar to see all numbers or tap arrows 👈",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        // 2. Dynamic Input Keyboard Row with left/right quick scroll buttons & dynamic sizing
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        val buttonSize = when (gridSize) {
            9 -> 36.dp
            4 -> 48.dp
            else -> 42.dp
        }
        val buttonSpacing = when (gridSize) {
            9 -> 5.dp
            else -> 8.dp
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Left Quick Scroll Button
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(0)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◀",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Scrollable Keyboard keys in the middle
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState)
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (num in 1..gridSize) {
                    Box(
                        modifier = Modifier
                            .size(buttonSize)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f))
                            .clickable { onNumberEntered(num) }
                            .testTag("num_pad_$num"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Black,
                            fontSize = if (gridSize == 9) 18.sp else 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Right Quick Scroll Button
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (onToggleGridHelpers != null || onToggleHideLastRow != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onToggleGridHelpers != null) {
                    val helpColor = if (disableGridHelpers) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    val helpBg = if (disableGridHelpers) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    Surface(
                        color = helpBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onToggleGridHelpers() }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Grid Accent Highlights: " + (if (disableGridHelpers) "OFF" else "ON"),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = helpColor,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                if (onToggleHideLastRow != null) {
                    val rowColor = if (hideLastRow) Color(0xFFFF9800) else MaterialTheme.colorScheme.outline
                    val rowBg = if (hideLastRow) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant
                    Surface(
                        color = rowBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onToggleHideLastRow() }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Blind Last Row: " + (if (hideLastRow) "ACTIVE" else "SHOW ALL"),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = rowColor,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val iconColor = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = iconColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }

        if (badge != null) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
            ) {
                Text(
                    text = badge,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

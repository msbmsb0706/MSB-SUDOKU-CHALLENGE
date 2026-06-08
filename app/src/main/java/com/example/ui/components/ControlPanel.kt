package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core tools: Pencil Toggle, Erase, Hint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Pencil Toggle Button
            ToolButton(
                icon = Icons.Default.Create,
                label = "Notes",
                isActive = pencilMode,
                onClick = onPencilToggle,
                badge = if (pencilMode) "ON" else null,
                testTag = "pencil_tool_btn"
            )

            // Erase / Clear Button (Highlighted with a custom subtle color for priority erase action!)
            ToolButton(
                icon = Icons.Default.Clear,
                label = "Erase",
                isActive = false,
                onClick = onClear,
                badge = null,
                testTag = "erase_tool_btn"
            )

            // Hint Button
            ToolButton(
                icon = Icons.Default.Info,
                label = "Hint",
                isActive = false,
                onClick = onHint,
                badge = "${gemsRemaining}G", // displays remaining gems
                testTag = "hint_tool_btn"
            )
        }

        // Dynamic Input Keyboard Grid (1..4 for Quick grid, 1..9 for Standard matrix!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (num in 1..gridSize) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
                        .clickable { onNumberEntered(num) }
                        .testTag("num_pad_$num"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = num.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (gridSize == 4) 28.sp else 22.sp
                    )
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
    testTag: String
) {
    val containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val iconColor = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .width(96.dp)
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
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

package com.ccino.demo.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Label(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .padding(2.dp)
            .border(BorderStroke(1.dp, Color.Red), RoundedCornerShape(4.dp))
            .padding(2.dp)
            .clickable { onClick() },
        fontSize = 8.sp
    )
}
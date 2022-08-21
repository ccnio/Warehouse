package com.ccnio.ware.compose.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ccnio.ware.compose.ui.theme.Purple200

@Composable
fun SpanText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .padding(2.dp)
            .border(BorderStroke(1.dp, Purple200), RoundedCornerShape(4.dp))
            .padding(2.dp),
        fontSize = 10.sp
    )
}
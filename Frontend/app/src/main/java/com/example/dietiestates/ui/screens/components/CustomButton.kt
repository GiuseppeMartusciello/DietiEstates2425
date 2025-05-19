package com.example.dietiestates.ui.screens.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietiestates.ui.screens.BottomBar
import com.example.dietiestates.ui.theme.RobotoMono


@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String? = null,
    icon: ImageVector? = null,
    @DrawableRes imageRes: Int? = null,
    modifier: Modifier = Modifier,
    style: String,
    enabled: Boolean = true
) {
    val backgroundColor = if (style == "white") Color.White else Color(0xFF3F51B5)
    val contentColor = if (style == "white") Color(0xFF3F51B5) else Color.White
    val border = if (style == "white") BorderStroke(1.dp, Color(0xFF9E9E9E)) else null

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
        shape = RoundedCornerShape(8.dp),
        border = border,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(28.dp))
        }
        if (imageRes != null ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        if (text != null) {
            if (icon != null) Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontFamily = RobotoMono, fontWeight = FontWeight.Medium, lineHeight = 28.sp, fontSize = 18.sp)
        }
    }
}




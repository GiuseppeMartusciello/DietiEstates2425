package com.example.dietiestates.ui.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietiestates.R
import com.example.dietiestates.ui.theme.RobotoMono

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF9E9E9E)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor =Color.White,
            contentColor = Color(0xFF3F51B5)
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.android_light_rd_na),
            contentDescription = "Google Logo",
            modifier = Modifier
                .size(28.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "ACCEDI CON GOOGLE",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = Color(0xFF3F51B5),
            lineHeight = 28.sp
        )
    }
}

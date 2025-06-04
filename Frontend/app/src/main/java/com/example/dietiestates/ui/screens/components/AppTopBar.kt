package com.example.dietiestates.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab


@Composable
fun AppTopBar(modifier: Modifier) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(0.dp, 0.dp, 0.dp, 8.dp)
            .background(Color(0xFF3F51B5)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "DietiEstates25", fontFamily = RobotoSerif,
            fontWeight = FontWeight.SemiBold, fontSize = 40.sp, color = Color.White
        )
        Text(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 5.dp),
            text = "Perchè perder tempo quando ci siamo noi?", fontFamily = RobotoSlab,
            fontWeight = FontWeight.Normal, fontSize = 16.sp, color = Color.White
        )
    }
}
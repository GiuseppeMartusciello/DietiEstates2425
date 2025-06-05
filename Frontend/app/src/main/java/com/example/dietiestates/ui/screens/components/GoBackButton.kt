package com.example.dietiestates.ui.screens.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GoBackButton(
    modifier: Modifier,
    navController: NavController,
    root: String
)
{
    IconButton(
        onClick = { navController.navigate(root) },
        modifier = modifier
            .size(32.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF3F51B5), // Colore personalizzato del bordo
                shape = CircleShape // o RoundedCornerShape(6.dp) per bordi morbidi
            )
            .padding(4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBackIosNew,
            contentDescription = "Torna indietro",
            tint = Color(0xFF3F51B5),
            modifier = Modifier.size(18.dp)

        )
    }

}

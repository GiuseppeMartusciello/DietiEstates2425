package com.example.dietiestates.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GoBackButton(
    navController: NavController,
    root: String = ""
)
{
    Box() {
        IconButton(
            onClick = {
                if (root.isNotEmpty()) {
                    navController.navigate(root) {
                        popUpTo(root) { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    navController.navigateUp()
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = 0.dp,
                    start = 8.dp
                )
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    clip = false
                )
                .background(
                    color = Color(0xFF3F51B5),
                    shape = CircleShape
                )
                .size(40.dp),

            ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Chiudi",
                tint = Color.White
            )

        }
    }
}

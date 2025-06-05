package com.example.dietiestates.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.AppBottomBar
import com.example.dietiestates.ui.screens.components.AppTopBar
import com.example.dietiestates.ui.viewModel.NotificationViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dietiestates.data.model.Notification
import com.example.dietiestates.ui.viewModel.NotificationState


@Composable
fun NotificationScreen(navController: NavController,viewModel: NotificationViewModel) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Scaffold(
        topBar = { AppTopBar(modifier = Modifier) },
        bottomBar = { AppBottomBar(navController) }
    )
    { paddingValues ->

        when (state) {
            is NotificationState.Loading -> CircularProgressIndicator()
            is NotificationState.Notifications -> {
                val notifications = (state as NotificationState.Notifications).notifications
                NotificationScroll(notifications,paddingValues)
            }

            is NotificationState.Error -> {
                val message = (state as NotificationState.Error).message
                Text(text = message, color = Color.Red)
            }
        }
    }
}

@Composable
fun NotificationScroll(notifications: List<Notification>, paddingValues: PaddingValues)
{
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 2.dp)
    ) {


    }
}
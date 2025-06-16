package com.example.dietiestates.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.data.model.Notification
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.NotificationState
import com.example.dietiestates.ui.viewModel.NotificationViewModel
import com.example.tuaapp.ui.components.NavBar
import kotlinx.coroutines.delay


@Composable
fun NotificationScreen(navController: NavController) {

    val viewModel: NotificationViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    var selectedNotification by remember { mutableStateOf<Notification?>(null) }
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Scaffold(
        topBar = {
            TopBarOffer(
                navController = navController,
                modifier = Modifier,
                "Centro Notifiche"
            )
        },
        bottomBar = { NavBar(navController = navController) }
    )
    { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp,
                    bottom = paddingValues.calculateBottomPadding()
                )
        )
        {
            when (state) {

                is NotificationState.Loading -> CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )

                is NotificationState.Notifications -> {
                    val notifications = (state as NotificationState.Notifications).notifications

                    NotificationScroll(
                        notifications,
                        onClickNotification = { notification ->
                            selectedNotification = notification
                            showDialog = true
                        },
                        isLoadingMore = true,
                        viewModel
                    )
                }

                is NotificationState.Error -> {
                    val message = (state as NotificationState.Error).message
                    Text(
                        text = message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

        }

        if (showDialog && selectedNotification != null) {
            NotificationDialog(
                notification = selectedNotification!!,
                onDismiss = {
                    showDialog = false
                    selectedNotification = null
                },
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun NotificationScroll(
    notifications: List<Notification>,
    onClickNotification: (Notification) -> Unit,
    isLoadingMore: Boolean,
    viewModel: NotificationViewModel
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(notifications) { notification ->
                NotificationItem(
                    notification,
                    onClick = { onClickNotification(notification) }
                )
            }

            // Mostra caricamento sempre in fondo
            if (isLoadingMore) {
                item {
                    NotificationLoading(listState, notifications, viewModel)
                }
            }
        }
    }
}


@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .heightIn(min = 64.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F3F3)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(end = 12.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifica",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Column() {
                    Text(
                        text = notification.title,
                        fontFamily = RobotoSlab,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                    Text(
                        text = notification.description,
                        fontFamily = RobotoSlab,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }

            }
            if (notification.userNotifications.isNotEmpty() && !notification.userNotifications[0].isRead) {

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF2C3A94), // Bordo più scuro
                            shape = CircleShape
                        )
                        .background(Color(0xFF3F51B5), shape = CircleShape)
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun NotificationDialog(
    notification: Notification,
    onDismiss: () -> Unit,
    navController: NavController,
    viewModel: NotificationViewModel
) {

    LaunchedEffect(notification.userNotifications[0].id) {
        if (!notification.userNotifications[0].isRead) viewModel.markAsRead(notification.userNotifications[0].id)
    }

    val title = notification.title
    val description = notification.description


    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                if (notification.category != "PROMOTIONAL") {
                    TextButton(
                        modifier = Modifier,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3F51B5),
                            contentColor = Color.White
                        ),
                        onClick = {
                            if (notification.category == "SEARCH") {
                                Log.d("output","id: ${notification.listing?.id}")
                                navController.navigate("listingscreen/${notification.listing?.id}")
                            }else if (notification.category == "OFFER")
                                navController.navigate("offer")
                        }
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .align(Alignment.CenterVertically),
                            color = Color.White,
                            text = if (notification.category == "SEARCH") {
                                "Vai alla proprietà"
                            } else {
                                "Vai all'offerta"
                            },
                            fontFamily = RobotoSlab,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onDismiss) {
                    Text("Chiudi")
                }
            }
        },
        title = {
            Text(
                text = title,
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = description,
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            )
        }
    )
}

@Composable
fun NotificationLoading(
    listState: LazyListState,
    notifications: List<Notification>,
    viewModel: NotificationViewModel
) {
    var showLoading by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var triggerCount by remember { mutableIntStateOf(0) }

    // Determina se siamo in fondo alla lista
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisible >= notifications.lastIndex
        }
    }

    // Trigger incrementale ogni volta che si arriva in fondo
    LaunchedEffect(isAtBottom) {
        if (isAtBottom) {
            triggerCount++
        }
    }

    // Quando il trigger cambia, mostra loading per 2s, poi messaggio
    LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            showMessage = false
            showLoading = true
            delay(2000)
            showLoading = false
            showMessage = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .heightIn(min = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            showLoading -> CircularProgressIndicator()
            showMessage -> {
                Text(
                    text = "Le notifiche sono state aggiornate",
                    fontFamily = RobotoSlab,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                //viewModel.loadNotifications()
            }
        }
    }
}





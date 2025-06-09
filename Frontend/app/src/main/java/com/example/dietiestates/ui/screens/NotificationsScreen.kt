package com.example.dietiestates.ui.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.AppBottomBar
import com.example.dietiestates.ui.screens.components.AppTopBar
import com.example.dietiestates.ui.viewModel.NotificationViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dietiestates.data.model.Notification
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.NotificationState


@Composable
fun NotificationScreen(navController: NavController) {

   val viewModel: NotificationViewModel  = viewModel()
   val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = { AppBottomBar(navController) }
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
                    NotificationScroll(notifications,navController)
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
    }
}


@Composable
fun NotificationScroll(notifications: List<Notification>,navController: NavController)
{
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(vertical = 0.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {

            notifications.isEmpty() -> {
               item() {
                   Text(
                       text = "Non Hai ancora nessuna notifica",
                       fontFamily = RobotoSlab,
                       fontWeight = FontWeight.Normal,
                       modifier = Modifier.fillMaxWidth(),
                       textAlign = TextAlign.Center
                   )
               }
            }

            else -> {

                items(notifications) { notification ->
                    NotificationItem(
                        notification,
                        navController = navController
                    )
                }
                item()
                {
                        Text(
                            text = "Non ci sono Altre notifiche",
                            fontFamily = RobotoSlab,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 32.dp),
                            textAlign = TextAlign.Center
                        )
                }
            }
        }
    }
}




@Composable
fun NotificationItem(notification: Notification,navController: NavController) {

        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            NotificationDialog(
                notification,
                onDismiss = { showDialog = false},
                navController
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .heightIn(min = 64.dp)
                .clickable { showDialog = true },
            shape = RectangleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = notification.title,
                        fontFamily = RobotoSlab,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
//metto [0] perche la get resituisce un array di usernotification in cui ci sta come user notification
// solo la usernotification dello user che fa la query

                }
                if (notification.userNotifications.isNotEmpty() && !notification.userNotifications[0].isRead) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
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
    navController: NavController
) {

    val title = notification.title
    val description = notification.description
    val isPromotional = notification.category == "PROMOTIONAL"

    notification.category

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
                Row {
                    TextButton(
                        modifier = Modifier,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3F51B5),
                            contentColor = Color.White
                        ),
                        onClick = {
                            if (isPromotional) {
                                Log.e("NotificationDialog", "ID listing ${notification.listing?.id}")

                               // navController.navigate("listingscreen/${notification.listing?.id}");
                            }
                        }
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .align(Alignment.CenterVertically),
                            color = Color.White,
                            text = if  (isPromotional) "Vai alla proprietà" else "Vai all'offerta",
                            fontFamily = RobotoSlab,
                            fontWeight = FontWeight.Bold,
                        )
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



package com.example.dietiestates.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.theme.customBlue
import com.example.dietiestates.ui.viewModel.AuthViewModel
import com.example.dietiestates.ui.viewModel.ChangePasswordState
import kotlinx.coroutines.launch


@Composable()
fun ChangePassword(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val currentPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val changePasswordState by viewModel.changePasswordState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(changePasswordState) {
        when (changePasswordState) {
            is ChangePasswordState.Success -> {
                navController.navigate("home") {
                    popUpTo("changepasswordscreen") { inclusive = true }
                }
            }

            is ChangePasswordState.Error -> {
                val msg = (changePasswordState as ChangePasswordState.Error).message
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(msg)
                }
            }

            else -> {}
        }
    }


    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = customBlue,
        unfocusedBorderColor = customBlue,
        cursorColor = customBlue,
        focusedLabelColor = customBlue,
        unfocusedLabelColor = customBlue,
        focusedLeadingIconColor = customBlue,
        unfocusedLeadingIconColor = customBlue,
        errorBorderColor = MaterialTheme.colorScheme.error,
        errorLabelColor = MaterialTheme.colorScheme.error,
        errorLeadingIconColor = MaterialTheme.colorScheme.error
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Text("Inserisci la password attuale")

        OutlinedTextField(
            value = currentPassword.value,
            onValueChange = { currentPassword.value = it },
            label = { Text("Password attuale") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                val icon =
                    if (passwordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = {
                    passwordVisible.value = !passwordVisible.value
                }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Text("Inserisci la nuova password")

        OutlinedTextField(
            value = newPassword.value,
            onValueChange = { newPassword.value = it },
            label = { Text("Nuova password") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                val icon =
                    if (passwordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = {
                    passwordVisible.value = !passwordVisible.value
                }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        CustomButton(
            onClick = {
                viewModel.changePassword(
                    currentPassword = currentPassword.value,
                    newPassword = newPassword.value)

            },
            style= "blue", text = "Conferma")
    }
}

package com.example.dietiestates.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.ui.viewModel.AuthViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.dietiestates.data.model.SignUpRequest
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.LoginState
import com.example.dietiestates.ui.viewModel.RegistrationState
import com.example.dietiestates.ui.viewModel.RegistrationViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegistrationViewModel = viewModel()) {
    val context = LocalContext.current
    val name by viewModel::name
    val surname by viewModel::surname
    val email by viewModel::email
    val password by viewModel::password
    val passwordVisible by viewModel::passwordVisible
    val phone by viewModel::phone

    var error by remember { mutableStateOf<String?>(null) }
    val registrationState by viewModel.registrationState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val snackbarHostState = remember { SnackbarHostState() }

    val customBlue = Color(0xFF3F51B5)

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

    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Transparent, darkIcons = true
        ) // o false se immagine scura
    }

    LaunchedEffect(registrationState) {
        when (registrationState) {
            is RegistrationState.Success -> {
                navController.navigate("home") {
                    popUpTo("loginscreen") { inclusive = true }
                }
            }

            is RegistrationState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Errore di accesso: ${(registrationState as RegistrationState.Error).message}")
                }
            }

            else -> {}
        }
    }


    Scaffold(

        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        topBar = {
            TopBarOffer(navController = navController, modifier = Modifier, text = "Registrazione")
        },

        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                //verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                CustomButton(
                    onClick = {

                        viewModel.register(SignUpRequest(name, surname, email, password, phone),
                            { errorMessage ->


                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(errorMessage)
                                }
                            })
                    }, style = "Blue", text = "REGISTRATI", modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {


            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp),
                //.weight(3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Registrazione",
                    fontFamily = RobotoSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 40.sp,
                    color = Color(0xFF3F51B5)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    label = { Text("Nome") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )


                //Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = surname,
                    onValueChange = { viewModel.onSurnameChanged(it) },
                    label = { Text("Cognome") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )


                //Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    value = email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )


                //Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    value = password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = { Text("Password") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        val icon =
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(imageVector = icon, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                //Spacer(modifier = Modifier.height(8.dp))


                OutlinedTextField(
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = phone,
                    onValueChange = { viewModel.onPhoneChanged(it) },
                    label = { Text("Telefono") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                error?.let {
                    Text(text = it, color = Color.Red)
                }




                if (registrationState is RegistrationState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))



            }

        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            snackbar = { snackbarData ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = snackbarData.visuals.message,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        snackbarData.visuals.actionLabel?.let { actionLabel ->
                            TextButton(onClick = { snackbarData.performAction() }) {
                                Text(actionLabel.uppercase(), color = Color.Yellow)
                            }
                        }
                    }
                }
            }
        )
    }
}


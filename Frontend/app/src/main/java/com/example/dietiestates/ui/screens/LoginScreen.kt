package com.example.dietiestates.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.viewModel.AuthViewModel
import com.example.dietiestates.ui.viewModel.LoginState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.dietiestates.R
import com.example.dietiestates.data.model.PostLoginNavigation
import com.example.dietiestates.ui.screens.components.GoogleSignInButton
import com.example.dietiestates.ui.theme.customBlue
import com.example.dietiestates.utility.GoogleSignInUtil
import kotlinx.coroutines.delay


@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val googleSignInClient = remember { GoogleSignInUtil.getClient(context) }
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    // All'inizio del LoginScreen
    val email by viewModel::email
    val password by viewModel::password
    val passwordVisible by viewModel::passwordVisible


    val loginState by viewModel.loginState.collectAsState()
    val postLoginDestination by viewModel.postLoginNavigation

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data,context)
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



    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Transparent,
            darkIcons = true
        ) // o false se immagine scura
    }


    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                when (postLoginDestination) {
                    PostLoginNavigation.HOME -> {
                        navController.navigate("home") {
                            popUpTo("loginscreen") { inclusive = true }
                        }
                    }

                    PostLoginNavigation.CHANGE_PASSWORD -> {
                        navController.navigate("changepasswordscreen") {
                            popUpTo("loginscreen") { inclusive = true }
                        }
                    }
                }

            }
            is LoginState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Errore di accesso: ${(loginState as LoginState.Error).message}")
                }
            }

            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),

            ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.logodieti),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(200.dp)
                            .width(200.dp)
                    )
                    Text(
                        text = "DietiEstates25",
                        fontFamily = RobotoSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 45.sp,
                        color = Color(0xFF3F51B5),
                    )

                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .weight(3f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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


                    Spacer(modifier = Modifier.height(8.dp))

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


                    Spacer(modifier = Modifier.height(20.dp))

                    LoginButton(
                        viewModel = viewModel,
                        loginState = loginState,
                        context = context,
                        email = email
                    )

                    if (loginState is LoginState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                    }

                }

                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    GoogleSignInButton(onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    })

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomButton(
                        onClick = {
                            viewModel.resetLoginState()
                            navController.navigate("registerscreen");
                        },
                        style = "white",
                        text = "REGISTRATI",
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    )

                }

                Spacer(modifier = Modifier.height(30.dp))
            }


        }



        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .align(Alignment.TopCenter),
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



@Composable
fun LoginButton(viewModel: AuthViewModel, loginState: LoginState, context: Context, email: String) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isClickable by remember { mutableStateOf(true) }

    CustomButton(
        onClick = {
            if (isClickable) {
                isClickable = false // disabilita subito
                coroutineScope.launch {
                    // riabilita dopo 500 ms
                    delay(500)
                    isClickable = true
                }

                if (viewModel.validateInputs { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                ) {
                    viewModel.login(email, viewModel.password, context)
                }
            }
        },
        style = "blue",
        enabled = loginState !is LoginState.Loading && isClickable,
        text = "ACCEDI",
        modifier = Modifier.fillMaxWidth()
    )
}

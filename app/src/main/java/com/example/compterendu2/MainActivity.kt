package com.example.compterendu2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LightPurple = Color(0xFFE4D7FF)
val LightGreen = Color(0xFFDFFFD7)
val SoftMint = Color(0xFFD2FFDA)
val SoftCoral = Color(0xFFFFC1B6)
val WarningRed = Color(0xFFFF4C4C)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("Login") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Gray, Color.Red)
                )
            )
    ) {
        when (currentScreen) {
            "Login" -> LoginScreen(
                onLoginSuccess = { currentScreen = "Home" },
                onForgotPasswordClick = { currentScreen = "ResetPassword" },
                onSignUpClick = { currentScreen = "SignUp" }
            )
            "Home" -> HomeScreen(onLogoutClick = { currentScreen = "Login" })
            "ResetPassword" -> ResetPasswordScreen(
                onBackClick = { currentScreen = "Login" }
            )
            "SignUp" -> SignUpScreen(
                onBackClick = { currentScreen = "Login" }
            )
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BasicTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (email.isEmpty()) {
                        Text("Email", fontSize = 20.sp, color = Color.White)
                    }
                    innerTextField()
                }
            }
        )
        if (emailError) {
            Text("Invalid email format", color = WarningRed, fontSize = 16.sp)
        }

        BasicTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (password.isEmpty()) {
                        Text("Password", fontSize = 20.sp, color = Color.White)
                    }
                    innerTextField()
                }
            }
        )
        if (passwordError) {
            Text("Password cannot be empty", color = WarningRed, fontSize = 16.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = passwordVisible, onCheckedChange = { passwordVisible = it })
            Text("Show Password", color = Color.White)
        }

        Button(
            onClick = {
                emailError = !isValidEmail(email)
                passwordError = password.isEmpty()
                if (!emailError && !passwordError) {
                    onLoginSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SoftMint)
        ) {
            Text("Log In", fontSize = 20.sp, color = Color.Black)
        }

        TextButton(onClick = onSignUpClick) {
            Text("Sign Up", color = Color.White, fontSize = 18.sp)
        }

        TextButton(onClick = onForgotPasswordClick) {
            Text("Forgot Password?", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun HomeScreen(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Home", fontSize = 24.sp, color = Color.White)

        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightPurple)
        ) {
            Text("Logout", fontSize = 20.sp, color = Color.Black)
        }
    }
}

@Composable
fun ResetPasswordScreen(onBackClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var emailSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reset Password", fontSize = 30.sp, color = Color.White)

        if (!emailSent) {
            // Email Field
            BasicTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (email.isEmpty()) {
                            Text("Email", fontSize = 20.sp, color = SoftCoral)
                        }
                        innerTextField()
                    }
                }
            )
            if (emailError) {
                Text("Invalid email format", color = WarningRed, fontSize = 16.sp)
            }

            Button(
                onClick = {
                    emailError = !isValidEmail(email)
                    if (!emailError) {
                        emailSent = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftCoral)
            ) {
                Text("Reset Password", fontSize = 20.sp, color = Color.Black)
            }
        } else {
            Text(
                "A password reset link has been sent to $email",
                fontSize = 18.sp,
                color = LightGreen
            )
        }

        TextButton(onClick = onBackClick) {
            Text("Back to Login", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun SignUpScreen(onBackClick: () -> Unit) {
    var name by remember { mutableStateOf("") } // New Name field
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", fontSize = 30.sp, color = Color.White)

        BasicTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (name.isEmpty()) {
                        Text("Name", fontSize = 20.sp, color = Color.White)
                    }
                    innerTextField()
                }
            }
        )
        if (nameError) {
            Text("Name cannot be empty", color = WarningRed, fontSize = 16.sp)
        }

        BasicTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (email.isEmpty()) {
                        Text("Email", fontSize = 20.sp, color = Color.White)
                    }
                    innerTextField()
                }
            }
        )
        if (emailError) {
            Text("Invalid email format", color = WarningRed, fontSize = 16.sp)
        }

        BasicTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (password.isEmpty()) {
                        Text("Password", fontSize = 20.sp, color = Color.White)
                    }
                    innerTextField()
                }
            }
        )
        if (passwordError) {
            Text("Password cannot be empty", color = WarningRed, fontSize = 16.sp)
        }

        BasicTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (confirmPassword.isEmpty()) {
                        Text("Confirm Password", fontSize = 20.sp, color = Color.White)
                    }
                    innerTextField()
                }
            }
        )
        if (confirmPasswordError) {
            Text("Passwords do not match", color = WarningRed, fontSize = 16.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = passwordVisible, onCheckedChange = { passwordVisible = it })
            Text("Show Password", color = Color.White)
        }

        Button(
            onClick = {
                nameError = name.isEmpty()
                emailError = !isValidEmail(email)
                passwordError = password.isEmpty()
                confirmPasswordError = confirmPassword != password

                if (!nameError && !emailError && !passwordError && !confirmPasswordError) {
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
        ) {
            Text("Sign Up", fontSize = 20.sp, color = Color.Black)
        }

        TextButton(onClick = onBackClick) {
            Text("Back to Login", color = Color.White, fontSize = 18.sp)
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}

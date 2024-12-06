package com.example.compterendu

import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.compterendu.db.FeedReaderDbHelper
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity


val LightPurple = Color(0xFFE4D7FF)
val LightGreen = Color(0xFFDFFFD7)
val SoftMint = Color(0xFFD2FFDA)
val SoftCoral = Color(0xFFFFC1B6)
val WarningRed = Color(0xFFFF4C4C)

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val savedEmail = sharedPreferences.getString("email", "") ?: ""
        val savedPassword = sharedPreferences.getString("password", "") ?: ""
        val dbHelper = FeedReaderDbHelper(this)
        val db = dbHelper.writableDatabase
        val productList = getAllProducts(db)
        insertProduct(db, "Pc Apple MacBook Air 13", "3.650.00", " Solutions compactes pour le streaming, la bureautique ou une utilisation média..", R.drawable.laptop)
        insertProduct(db, "Pc Portable HP 15-FC0018NK", "1.300.00", "Machines haut de gamme, ultra-légères et performantes, adaptées aux professionnels en déplacement.", R.drawable.laptop1)
        insertProduct(db, "Pc Gamer Lenovo IdeaPad Gaming 3", "2.180.000", "Conçues pour les professionnels nécessitant des calculs intensifs (graphisme 3D, modélisation, simulation).", R.drawable.laptop2)
        insertProduct(db, "Pc Portable HP", "1.750.00", "Portable et adapté aux déplacements, convenant aux étudiants, professionnels et nomades.", R.drawable.laptop3)
        insertProduct(db, "Pc Portable Gamer - MSI", "2.840.00", " Conçu pour les jeux vidéo exigeants et les tâches graphiques lourdes.", R.drawable.laptop4)
        insertProduct(db, "Pc Portable Lenovo IdeaPad 1 ", "780.000", "Idéal pour une utilisation à la maison ou au bureau, incluant les travaux professionnels, le gaming ou le développement..", R.drawable.laptop5)



        setContent {
            val initialScreen = if (isLoggedIn) "Home" else "Login"
            MainScreen(
                productList = productList,
                savedEmail = savedEmail,
                savedPassword = savedPassword,
                sharedPreferences = sharedPreferences,
                initialScreen = initialScreen
            )
        }
    }

}

fun insertProduct(db: SQLiteDatabase, name: String, price: String, description: String, imageRes: Int) {
    val cursor = db.rawQuery("SELECT * FROM Product WHERE name = ?", arrayOf(name))
    if (cursor.count == 0) {
        val sql = "INSERT INTO Product (name, price, description, imageRes) VALUES (?, ?, ?, ?)"
        val statement = db.compileStatement(sql)
        statement.bindString(1, name)
        statement.bindString(2, price)
        statement.bindString(3, description)
        statement.bindLong(4, imageRes.toLong())
        statement.executeInsert()
    }
    cursor.close()
}

fun getAllProducts(db: SQLiteDatabase): List<Product> {
    val productSet = mutableSetOf<Product>()
    val cursor = db.rawQuery("SELECT * FROM Product", null)
    with(cursor) {
        while (moveToNext()) {
            val name = getString(getColumnIndexOrThrow("name"))
            val price = getString(getColumnIndexOrThrow("price"))
            val description = getString(getColumnIndexOrThrow("description"))
            val imageRes = getInt(getColumnIndexOrThrow("imageRes"))
            productSet.add(Product(name, price, imageRes, description))
        }
        close()
    }
    Log.d("ProductList", "Products: $productSet")
    return productSet.toList()
}
@Composable
fun MainScreen(
    productList: List<Product>,
    savedEmail: String,
    savedPassword: String,
    sharedPreferences: SharedPreferences,
    initialScreen: String
) {
    var currentScreen by remember { mutableStateOf(initialScreen) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color.Gray, Color.Blue)))
    ) {
        when (currentScreen) {
            "Login" -> LoginScreen(
                onLoginSuccess = {
                    // Mettre à jour l'état de connexion dans SharedPreferences
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", true)
                        apply()
                    }
                    currentScreen = "Home"
                },
                onForgotPasswordClick = { currentScreen = "ResetPassword" },
                onSignUpClick = { currentScreen = "SignUp" },
                sharedPreferences = sharedPreferences,
                savedEmail = savedEmail,
                savedPassword = savedPassword
            )
            "Home" -> HomeScreen(
                productList = productList,
                onLogoutClick = {
                    // Déconnecter l'utilisateur
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false)
                        remove("email")
                        remove("password")
                        apply()
                    }
                    currentScreen = "Login"
                }
            )
        }
    }
}



@Composable
fun LoginScreen(
    sharedPreferences: SharedPreferences,

    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    savedEmail: String,
    savedPassword: String
) {
    var email by remember { mutableStateOf(savedEmail) }  // Initialiser avec l'email sauvegardé
    var password by remember { mutableStateOf(savedPassword) }
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
                    // Sauvegarder les données de connexion
                    with(sharedPreferences.edit()) {
                        putString("email", email)
                        putString("password", password)
                        putBoolean("isLoggedIn", true) // Marquer comme connecté
                        apply()
                    }
                    onLoginSuccess() // Passer à la page d'accueil
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
fun HomeScreen(productList: List<Product>, onLogoutClick: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Store",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(productList) { product ->
                ProductCard(product)
            }
        }

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
fun ProductCard(product: Product) {
    var showDescription by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showDescription = !showDescription }
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (showDescription) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.description,
                            fontSize = 14.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = product.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = product.price,
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun ForgetPasswordClick(onBackClick: () -> Unit, onResetComplete: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reset Password", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (email.isEmpty()) Text("Enter your email", fontSize = 16.sp, color = Color.Gray)
                    innerTextField()
                }
            }
        )
        if (emailError) {
            Text("Invalid email format", color = WarningRed, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                emailError = !isValidEmail(email)
                if (!emailError) {
                    // Simulez une action de réinitialisation
                    onResetComplete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Password", fontSize = 18.sp)
        }

        TextButton(onClick = onBackClick) {
            Text("Back", color = Color.White)
        }
    }
}


@Composable
fun SignUpScreen(onSignUpComplete: () -> Unit, onBackClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
        Text("Sign Up", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (email.isEmpty()) Text("Email", fontSize = 16.sp, color = Color.Gray)
                    innerTextField()
                }
            }
        )
        if (emailError) {
            Text("Invalid email format", color = WarningRed, fontSize = 14.sp)
        }

        BasicTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (password.isEmpty()) Text("Password", fontSize = 16.sp, color = Color.Gray)
                    innerTextField()
                }
            }
        )
        if (passwordError) {
            Text("Password cannot be empty", color = WarningRed, fontSize = 14.sp)
        }

        BasicTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (confirmPassword.isEmpty()) Text("Confirm Password", fontSize = 16.sp, color = Color.Gray)
                    innerTextField()
                }
            }
        )
        if (confirmPasswordError) {
            Text("Passwords do not match", color = WarningRed, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                emailError = !isValidEmail(email)
                passwordError = password.isEmpty()
                confirmPasswordError = password != confirmPassword
                if (!emailError && !passwordError && !confirmPasswordError) {
                    onSignUpComplete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up", fontSize = 18.sp)
        }

        TextButton(onClick = onBackClick) {
            Text("Back", color = Color.White)
        }
    }
}


fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
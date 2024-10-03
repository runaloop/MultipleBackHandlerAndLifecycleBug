package com.example.multiplebackhandlercheck

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.multiplebackhandlercheck.ui.theme.MultipleBackHandlerCheckTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultipleBackHandlerCheckTheme {
                Surface(Modifier.fillMaxSize()) {
                    val context = LocalContext.current
                    SampleApp()
                    BackHandler {
                        Toast.makeText(context, "1: This should never show 👎", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun SampleApp() {
    val navController = rememberNavController()
    NavHost(navController, "first") {
        composable("first") { Screen("first") }
        composable("second") { Screen("second") }
    }

    LaunchedEffect(Unit) {
        delay(100)
        navController.navigate("second")
    }
}

@Composable
fun Screen(name: String) {
    val context = LocalContext.current
    BackHandler {
        Toast.makeText(context, "2: Back clicked from screen: $name 👍", Toast.LENGTH_SHORT).show()
    }
    Column(modifier = Modifier.padding(56.dp)) {
        Text("$name screen")
    }
}


package com.example.multiplebackhandlercheck

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
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
                    val message1 = "1: This should never ever show!!!"
                    MyBackHandler(name = message1) {
                        Toast.makeText(context, message1, Toast.LENGTH_SHORT).show()
                    }
                    SampleApp()
                    val message2 = "2: This should never ever show!!!"
                    MyBackHandler(name = message2) {
                        Toast.makeText(context, message2, Toast.LENGTH_SHORT).show()
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
    val message3 = "3: Back clicked from $name"
    MyBackHandler(name = message3) {
        Toast.makeText(context, message3, Toast.LENGTH_SHORT).show()
    }
    AnotherComposable(name)
    val message5 = "5: Back clicked from $name"
    MyBackHandler(name = message5) {
        Toast.makeText(context, message5, Toast.LENGTH_SHORT).show()
    }
    Column {
        Text("$name screen")
    }
}

@Composable
fun AnotherComposable(name: String) {
    Text("Another Composss")
    val context = LocalContext.current
    val message4 = "4: Back clicked from super inner layer $name"
    MyBackHandler(name = message4) {
        Toast.makeText(context, message4, Toast.LENGTH_SHORT).show()
    }
}

@SuppressWarnings("MissingJvmstatic")
@Composable
public fun MyBackHandler(enabled: Boolean = true, name: String, onBack: () -> Unit) {
    // Safely update the current `onBack` lambda when a new one is provided
    println("🚒 MyBackHandler:: $name $onBack")
    val currentOnBack by rememberUpdatedState(onBack)
    // Remember in Composition a back callback that calls the `onBack` lambda
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                println("🚒 MyBackHandler::onBackHandler $name working $onBack")
                currentOnBack()
            }
        }
    }
    // On every successful composition, update the callback with the `enabled` value
    SideEffect {
        backCallback.isEnabled = enabled
    }
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        // Add callback to the backDispatcher
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            println("🚒 MyBackHandler::onDispose $name $onBack")
            backCallback.remove()
        }
    }
}

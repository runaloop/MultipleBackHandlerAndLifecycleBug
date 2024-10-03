Component used: Navigation

Version used: 2.8.2(previous version have same issue up to 2.7.7)

Devices/Android versions reproduced on: any

### Description
Documentation says: "If this is called by nested composables, if enabled, the inner most composable will consume the call to system back and invoke its lambda."
But that behavior changes if the activity is paused and resumed.

### Steps to reproduce:
1. Start the app.
2. Click the back button - now we can see that the inner most BackHandler has been triggered.
3. Minimize the app using the home button or press the recent button.
4. Bring app to foreground
5. Press the back button again - this time, the inner most BackHandler has not been triggered, and the call has propagated to the root level BackHandler.

### Code to demonstrate:

```kotlin
Surface(Modifier.fillMaxSize()) {  
    val context = LocalContext.current  
    SampleApp()  
    BackHandler {  
        Toast.makeText(context, "1: This should never show 👎", Toast.LENGTH_SHORT).show()// root BackHandler
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
```

### Desired behaviour:
Regardless of whether lifecycle methods are called, the priority of the BackHandler should be the same.


This issue is not same as issue - https://issuetracker.google.com/issues/279118447, it's different because in my case, the root BackHandler is overridden by the innermost BackHandler while navigation happens and the order of calls for the root Backhandler is different.

- Sample project to trigger the issue: [MultipleBackHandlerAndLifecycleBug](https://github.com/runaloop/MultipleBackHandlerAndLifecycleBug)
- A screenrecord or screenshots showing the issue (if UI related): [![Watch Video](https://img.youtube.com/vi/W4vm-hYhUo8/0.jpg)](https://youtu.be/W4vm-hYhUo8)

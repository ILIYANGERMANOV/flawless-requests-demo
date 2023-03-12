package com.flawlessrequests

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flawlessrequests.network.httpRequest
import com.flawlessrequests.network.ktorClient
import com.flawlessrequests.ui.theme.FlawlessRequestsDemoTheme
import io.ktor.client.request.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlawlessRequestsDemoTheme {
                // A surface container using the 'background' color from the theme
                LaunchedEffect(Unit) {
                    val res = httpRequest<String>(ktorClient()) {
                        get("https://www.google.com/")
                    }
                    Log.i("RES", res.toString())
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlawlessRequestsDemoTheme {
        Greeting("Android")
    }
}
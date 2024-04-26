package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun NoPermissionScreen(
    onRequestPermission: () -> Unit
) {
    val sharedPrefsManager = getSharedPrefsManager()
    val context = LocalContext.current
    val openUrlLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { /* handle result if needed */ }

    NoPermissionContent(
        onRequestPermission = onRequestPermission,
        sharedPrefsManager = sharedPrefsManager,
        openUrl = { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            openUrlLauncher.launch(intent)
        }
    )
}
// Saving the API key
@Composable
private fun getSharedPrefsManager(): SharedPrefsManager {
    val context = LocalContext.current
    return SharedPrefsManager(context)
}

@Composable
fun NoPermissionContent(
    onRequestPermission: () -> Unit,
    sharedPrefsManager: SharedPrefsManager,
    openUrl: (String) -> Unit // Simplified argument type
) {
    var apiKey by remember { mutableStateOf("") } // String type

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Improved Header Text with underline
        Text(
            text = "Steps to Use SnapLearn",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                textDecoration = TextDecoration.Underline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Numbered Steps with better clarity
        Text(text = "1. Obtain a Gemini API Key:")
        Button(onClick = {
            openUrl("https://aistudio.google.com/app/apikey")
        }) {
            Text("Get API Key") // Clearer button text
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "2. Enter the API Key here:")
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("Enter API Key") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            text = "3. Grant camera permission to use the core functionality."
        )
        Button(onClick = {
            onRequestPermission()
            sharedPrefsManager.storeApiKey(apiKey)
        }) {
            Icon(imageVector = Icons.Default.Camera, contentDescription = "Camera")
            Text(text = "Grant permission")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(20.dp),
            textAlign = TextAlign.Center,
            text = "Group 13 - SnapLearn, \n COST 32152, FOS, UOK | 2024",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
            )
        )
    }
}

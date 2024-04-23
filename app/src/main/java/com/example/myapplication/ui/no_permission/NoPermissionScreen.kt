package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera

@Composable
fun NoPermissionScreen(
    onRequestPermission: () -> Unit
) {
    NoPermissionContent(
        onRequestPermission = onRequestPermission
    )
}

@Composable
fun NoPermissionContent(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            text = "Please grant the permission to use the camera to use the core functionality of this app."
        )
        Button(onClick = onRequestPermission) {
            Icon(imageVector = Icons.Default.Camera, contentDescription = "Camera")
            Text(text = "Grant permission")
        }
    }
}
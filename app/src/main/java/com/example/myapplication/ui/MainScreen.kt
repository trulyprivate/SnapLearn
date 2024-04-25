@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.myapplication.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.myapplication.CameraScreen
import com.example.myapplication.NoPermissionScreen
import com.example.myapplication.SharedPrefsManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(sharedPrefsManager: SharedPrefsManager) {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)

    MainContent(
        sharedPrefsManager = sharedPrefsManager,
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )
}

@Composable
private fun MainContent(
    sharedPrefsManager: SharedPrefsManager,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    if (hasPermission) {
        CameraScreen(sharedPrefsManager = sharedPrefsManager)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}

@Preview
@Composable
private fun Preview_MainContent() {
    MainContent(
        sharedPrefsManager = SharedPrefsManager(context = LocalContext.current),
        hasPermission = true,
        onRequestPermission = {}
    )
}

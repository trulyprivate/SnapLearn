package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.darkColorScheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(sharedPrefsManager: SharedPrefsManager) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current // Obtain lifecycleOwner here
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }
    var detectedText by remember { mutableStateOf("No text detected yet..") }
    var isFlashlightOn by remember { mutableStateOf(false) }

    fun onTextUpdated(updatedText: String) {
        detectedText = updatedText
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Text Scanner") },
                backgroundColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary,
                actions = {
                    IconButton(
                        onClick = {
                            isFlashlightOn = !isFlashlightOn
                            toggleFlashlight(
                                context,
                                isFlashlightOn,
                                cameraController,
                                lifecycleOwner
                            )
                        }
                    ) {
                        Icon(
                            if (isFlashlightOn) Icons.Filled.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Flashlight"
                        )
                    }
                }
            )

        },
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(Color.TRANSPARENT)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        startTextRecognition(
                            context = context,
                            cameraController = cameraController,
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            onDetectedTextUpdated = ::onTextUpdated
                        )
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = detectedText,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                androidx.compose.material3.Button(
                    onClick = {
                        val apiKey = sharedPrefsManager.getApiKey() ?: ""
                        sharedPrefsManager.storeApiKey(apiKey)
                        val intent = Intent(context, SecondActivity::class.java).apply {
                            putExtra("detectedTextKey", detectedText)
                            putExtra("API_KEY", apiKey)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Bottom)
                ) {
                    Text("Ready")
                }

            }
        }
    }
}

private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (String) -> Unit
) {
    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated)
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}

fun toggleFlashlight(
    context: Context,
    turnOn: Boolean,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner // Add lifecycleOwner parameter
) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = cameraManager.cameraIdList[0]  // Assuming single camera

    // Check flashlight availability (optional)
    if (!cameraManager.getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.FLASH_INFO_AVAILABLE)!!
    ) {
        Toast.makeText(context, "Flashlight not available on this device", Toast.LENGTH_SHORT)
            .show()
        return
    }

    try {
        if (turnOn) {
            cameraManager.setTorchMode(cameraId, true)
        } else {
            cameraManager.setTorchMode(cameraId, false)
        }
    } catch (e: Exception) {
        val errorMessage = "Error controlling flashlight: ${e.message}"
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }
}


enum class FlashlightState {
    On, // Add On state
    Off // Add Off state
}
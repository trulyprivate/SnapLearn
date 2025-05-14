package com.example.snaplearn.utils

import java.util.UUID

actual fun generateUUID(): String = UUID.randomUUID().toString() 
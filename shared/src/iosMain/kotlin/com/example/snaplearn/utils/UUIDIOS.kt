package com.example.snaplearn.utils

import platform.Foundation.NSUUID

actual fun generateUUID(): String = NSUUID().UUIDString 
package com.example.snaplearn

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform 
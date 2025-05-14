package com.example.snaplearn

import android.app.Application
import com.example.snaplearn.di.initKoin
import com.example.snaplearn.di.initKoinAndroid

class SnapLearnApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        initKoin()
        initKoinAndroid(this)
    }
} 
package com.example.snaplearn.utils

import android.content.Context
import android.content.SharedPreferences

class AndroidStorage(private val context: Context) : Storage {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("SnapLearnPrefs", Context.MODE_PRIVATE)
    }

    override fun getString(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    override fun putString(key: String, value: String): Boolean {
        return prefs.edit().putString(key, value).commit()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun putInt(key: String, value: Int): Boolean {
        return prefs.edit().putInt(key, value).commit()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    override fun putLong(key: String, value: Long): Boolean {
        return prefs.edit().putLong(key, value).commit()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean): Boolean {
        return prefs.edit().putBoolean(key, value).commit()
    }

    override fun remove(key: String): Boolean {
        return prefs.edit().remove(key).commit()
    }

    override fun clear(): Boolean {
        return prefs.edit().clear().commit()
    }
}

// This will be initialized in the Android app
private lateinit var androidContext: Context

fun initStorage(context: Context) {
    androidContext = context.applicationContext
}

actual fun getStorage(): Storage {
    if (!::androidContext.isInitialized) {
        throw IllegalStateException("Storage not initialized. Call initStorage(context) first.")
    }
    return AndroidStorage(androidContext)
} 
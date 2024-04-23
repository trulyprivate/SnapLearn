package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getApiKey(): String? {
        return sharedPreferences.getString(API_KEY_KEY, null)
    }

    fun storeApiKey(apiKey: String) {
        sharedPreferences.edit().putString(API_KEY_KEY, apiKey).apply()
    }

    companion object {
        private const val PREF_NAME = "MyAppPrefs"
        private const val API_KEY_KEY = "API_KEY"
    }
}

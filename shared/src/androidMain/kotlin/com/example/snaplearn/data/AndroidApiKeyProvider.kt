package com.example.snaplearn.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of ApiKeyProvider that uses EncryptedSharedPreferences.
 */
class AndroidApiKeyProvider(private val context: Context) : ApiKeyProvider {
    
    companion object {
        private const val SHARED_PREFS_FILE = "snaplearn_secure_prefs"
        private const val KEY_API_KEY = "gemini_api_key"
    }
    
    /**
     * Gets a securely stored API key.
     */
    override suspend fun getApiKey(): String = withContext(Dispatchers.IO) {
        // Create or retrieve master key for encryption
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        // Initialize encrypted shared preferences
        val encryptedPrefs = EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        
        // Get API key from encrypted preferences
        val apiKey = encryptedPrefs.getString(KEY_API_KEY, null)
        
        if (apiKey.isNullOrEmpty()) {
            throw IllegalStateException("Gemini API key not found. Please set it first.")
        }
        
        return@withContext apiKey
    }
    
    /**
     * Sets the API key in secure storage.
     */
    suspend fun setApiKey(apiKey: String) = withContext(Dispatchers.IO) {
        // Create or retrieve master key for encryption
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        // Initialize encrypted shared preferences
        val encryptedPrefs = EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        
        // Save API key to encrypted preferences
        encryptedPrefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }
} 
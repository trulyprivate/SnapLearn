package com.example.snaplearn.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithCString
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * iOS implementation of ApiKeyProvider that uses Keychain.
 */
@OptIn(ExperimentalForeignApi::class)
class IOSApiKeyProvider : ApiKeyProvider {
    companion object {
        private const val SERVICE_NAME = "com.example.snaplearn"
        private const val ACCOUNT_NAME = "gemini_api_key"
    }

    /**
     * Gets the API key from Keychain.
     */
    override suspend fun getApiKey(): String {
        val key = retrieveFromKeychain(ACCOUNT_NAME)
        if (key.isEmpty()) {
            throw IllegalStateException("Gemini API key not found. Please set it first.")
        }
        return key
    }

    /**
     * Sets the API key in Keychain.
     */
    fun setApiKey(apiKey: String) {
        saveToKeychain(ACCOUNT_NAME, apiKey)
    }

    /**
     * Retrieves a string value from Keychain.
     */
    @ExperimentalForeignApi
    private fun retrieveFromKeychain(key: String): String {
        memScoped {
            val queryDict = mutableMapOf<CFStringRef, CFTypeRef?>(
                kSecClass as CFStringRef to kSecClassGenericPassword,
                kSecAttrService as CFStringRef to CFBridgingRetain(SERVICE_NAME),
                kSecAttrAccount as CFStringRef to CFBridgingRetain(key),
                kSecMatchLimit as CFStringRef to kSecMatchLimitOne,
                kSecReturnData as CFStringRef to kCFBooleanTrue
            )
            
            val queryDictRef = CFBridgingRetain(queryDict) as CFDictionaryRef
            val result = mutableListOf<CFTypeRef?>()
            
            val status = SecItemCopyMatching(queryDictRef, result.addressOf)
            
            if (status.toUInt() == 0u && result.isNotEmpty() && result[0] != null) {
                val data = CFBridgingRelease(result[0]) as? platform.Foundation.NSData
                if (data != null) {
                    val nsString = NSString.create(data = data, encoding = NSUTF8StringEncoding)
                    if (nsString != null) {
                        return nsString.toString()
                    }
                }
            }
            
            return ""
        }
    }

    /**
     * Saves a string value to Keychain.
     */
    @ExperimentalForeignApi
    private fun saveToKeychain(key: String, value: String) {
        // First, try to delete any existing item
        deleteFromKeychain(key)
        
        memScoped {
            // Convert string to NSData
            val nsValue = value as NSString
            val valueData = nsValue.dataUsingEncoding(NSUTF8StringEncoding)
                ?: throw IllegalArgumentException("Failed to encode string as UTF-8")
            
            // Create the query dictionary
            val queryDict = mutableMapOf<CFStringRef, CFTypeRef?>(
                kSecClass as CFStringRef to kSecClassGenericPassword,
                kSecAttrService as CFStringRef to CFBridgingRetain(SERVICE_NAME),
                kSecAttrAccount as CFStringRef to CFBridgingRetain(key),
                kSecValueData as CFStringRef to CFBridgingRetain(valueData)
            )
            
            val queryDictRef = CFBridgingRetain(queryDict) as CFDictionaryRef
            
            // Add the item to Keychain
            val status = SecItemUpdate(queryDictRef, queryDictRef)
            if (status.toUInt() != 0u) {
                SecItemDelete(queryDictRef) // Clean up if something went wrong
            }
        }
    }

    /**
     * Deletes a key from Keychain.
     */
    @ExperimentalForeignApi
    private fun deleteFromKeychain(key: String) {
        memScoped {
            val queryDict = mutableMapOf<CFStringRef, CFTypeRef?>(
                kSecClass as CFStringRef to kSecClassGenericPassword,
                kSecAttrService as CFStringRef to CFBridgingRetain(SERVICE_NAME),
                kSecAttrAccount as CFStringRef to CFBridgingRetain(key)
            )
            
            val queryDictRef = CFBridgingRetain(queryDict) as CFDictionaryRef
            
            SecItemDelete(queryDictRef)
        }
    }
} 
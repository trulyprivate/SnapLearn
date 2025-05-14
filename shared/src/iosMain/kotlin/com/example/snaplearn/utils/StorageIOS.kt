package com.example.snaplearn.utils

import platform.Foundation.NSUserDefaults

class IOSStorage : Storage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun getString(key: String, defaultValue: String): String {
        return userDefaults.stringForKey(key) ?: defaultValue
    }

    override fun putString(key: String, value: String): Boolean {
        userDefaults.setObject(value, key)
        return true
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return userDefaults.integerForKey(key).toInt()
    }

    override fun putInt(key: String, value: Int): Boolean {
        userDefaults.setInteger(value.toLong(), key)
        return true
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return userDefaults.integerForKey(key)
    }

    override fun putLong(key: String, value: Long): Boolean {
        userDefaults.setInteger(value, key)
        return true
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return userDefaults.boolForKey(key)
    }

    override fun putBoolean(key: String, value: Boolean): Boolean {
        userDefaults.setBool(value, key)
        return true
    }

    override fun remove(key: String): Boolean {
        userDefaults.removeObjectForKey(key)
        return true
    }

    override fun clear(): Boolean {
        val dictionary = userDefaults.dictionaryRepresentation()
        val keys = dictionary.keys
        keys.forEach { key ->
            if (key is String) {
                userDefaults.removeObjectForKey(key)
            }
        }
        return true
    }
}

actual fun getStorage(): Storage = IOSStorage() 
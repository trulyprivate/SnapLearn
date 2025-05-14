package com.example.snaplearn.utils

interface Storage {
    fun getString(key: String, defaultValue: String = ""): String
    fun putString(key: String, value: String): Boolean
    fun getInt(key: String, defaultValue: Int = 0): Int
    fun putInt(key: String, value: Int): Boolean
    fun getLong(key: String, defaultValue: Long = 0L): Long
    fun putLong(key: String, value: Long): Boolean
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun putBoolean(key: String, value: Boolean): Boolean
    fun remove(key: String): Boolean
    fun clear(): Boolean
}

expect fun getStorage(): Storage 
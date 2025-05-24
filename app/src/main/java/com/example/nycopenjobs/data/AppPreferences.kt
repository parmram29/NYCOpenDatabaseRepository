package com.example.nycopenjobs.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.nycopenjobs.util.TAG

/**
 * app shared preferences interface
 *
 *
 * This interface defines the shared preferences for the app
 */

interface AppSharedPreferences {
    fun getSharedPreferences(): SharedPreferences
}

/**
 * app shared preferences implementation
 *
 * this class implements the app shared preferences interface
 */

class AppPreferences(private val context: Context) : AppSharedPreferences {
    private val appPreferencesKey = "app_prefs"

    /**
     * Get the shared preferences
     *
     * @return the shared preferences
     */

    override fun getSharedPreferences(): SharedPreferences {
        Log.i(TAG, "getting shared preferences")
        return context.getSharedPreferences(appPreferencesKey, Context.MODE_PRIVATE)
    }
}

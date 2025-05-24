package com.example.nycopenjobs.util

import android.app.Application
import android.util.Log
import com.example.nycopenjobs.data.AppContainer
import com.example.nycopenjobs.data.DefaultAppContainer


/**
 * NYC Open jobs application
 */
class NycOpenJobsApplication : Application(){

    /**
     * app container
     *
     * this property is used to get the app container
     */

    lateinit var container: AppContainer


    /**
     * application OnCreate
     * initializes the app contatiner using the default app container
     *
     */
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Application: starting app")
        container = DefaultAppContainer(this)
    }
}
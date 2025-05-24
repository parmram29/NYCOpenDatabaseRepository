package com.example.nycopenjobs.data

import android.content.Context
import android.util.Log
import com.example.nycopenjobs.api.AppRemoteApis
import com.example.nycopenjobs.util.TAG

/**
 * App container
 *
 * this interface defines the app container
 */

interface AppContainer {
    val appRepository: AppRepository
}

/**
 * Default App Container
 *
 * this class defines the default app container. The container is responsible for providing
 * the app repository
 *
 * @param context the context of the app
 */

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val appRepository: AppRepository by lazy {
        Log.i(TAG, "initializing app repository")
        AppRepositoryImpl(
            AppRemoteApis().getNycOpenDataApi(),
            AppPreferences(context).getSharedPreferences(),
            LocalDatabase.getDatabase(context).jobPostDao()
        )
    }
}

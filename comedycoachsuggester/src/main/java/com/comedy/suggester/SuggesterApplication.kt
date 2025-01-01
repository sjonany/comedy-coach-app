package com.comedy.suggester

import android.app.Application
import com.comedy.suggester.data.AppContainer
import com.comedy.suggester.data.AppDataContainer

/**
 * I think this is for dependency injection setup?
 */
class SuggesterApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
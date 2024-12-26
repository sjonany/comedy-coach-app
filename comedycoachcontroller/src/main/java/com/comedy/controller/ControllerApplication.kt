package com.comedy.controller

import android.app.Application
import com.comedy.controller.data.AppContainer
import com.comedy.controller.data.AppDataContainer

/**
 * I think this is for dependency injection setup?
 */
class ControllerApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
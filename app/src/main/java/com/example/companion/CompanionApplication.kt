package com.example.companion

import android.app.Application
import com.example.companion.data.DataStoreManager
import com.example.companion.data.repository.CreatureRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CompanionApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(private val application: Application) {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val dataStoreManager = DataStoreManager(application)
    val creatureRepository = CreatureRepository(dataStoreManager, applicationScope)
}

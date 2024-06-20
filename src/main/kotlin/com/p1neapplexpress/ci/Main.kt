package com.p1neapplexpress.ci

import com.p1neapplexpress.ci.Storage.initDB
import com.p1neapplexpress.ci.bot.Core
import com.p1neapplexpress.ci.di.botModule
import com.p1neapplexpress.ci.di.localeManagerModule
import com.p1neapplexpress.ci.util.LocaleManager
import com.p1neapplexpress.ci.util.Log
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get
import java.io.FileNotFoundException

const val DEBUG = true

fun main() {
    initKoinDI()
    initDB()

    val core: Core = get(Core::class.java)
    core.initBot()
    core.startBot()

//    val watcher: RulesWatcher = get(RulesWatcher::class.java)
//    watcher.start()

    val localeManager: LocaleManager = get(LocaleManager::class.java)
    try {
        localeManager.loadConfig()
    } catch (e: FileNotFoundException) {
        Log.exitError("No locales file found")
    }
}

private fun initKoinDI() = startKoin {
    modules(botModule)
    modules(localeManagerModule)
}
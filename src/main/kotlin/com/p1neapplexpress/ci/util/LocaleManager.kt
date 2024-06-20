package com.p1neapplexpress.ci.util

import com.google.gson.Gson
import com.p1neapplexpress.ci.data.locale.Locale
import com.p1neapplexpress.ci.data.locale.LocaleConfig
import java.io.File

class LocaleManager {

    private lateinit var localeConfig: LocaleConfig
    private val localeConfigFile = File(LOCALES_FILE_PATH)

    fun loadConfig() {
        localeConfig = Gson().fromJson(localeConfigFile.readText(), LocaleConfig::class.java)
    }

    fun getLocaleEntry(locale: String, key: String): String =
        localeConfig.locales.first { it.lang == locale }.entries[key] ?: throw RuntimeException("No locale fount for key $key")

    fun getLocales(): List<Locale> = localeConfig.locales
    fun getDefaultLocale(): String = localeConfig.defaultLocale

    companion object {
        private const val LOCALES_FILE_PATH = "locales.json"
    }
}
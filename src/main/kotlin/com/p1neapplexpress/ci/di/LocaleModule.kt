package com.p1neapplexpress.ci.di

import com.p1neapplexpress.ci.util.LocaleManager
import org.koin.dsl.module

fun provideLocaleManager() = LocaleManager()

val localeManagerModule = module {
    single { provideLocaleManager() }
}
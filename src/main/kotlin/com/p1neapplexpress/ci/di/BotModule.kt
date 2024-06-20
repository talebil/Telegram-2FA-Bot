package com.p1neapplexpress.ci.di

import com.p1neapplexpress.ci.bot.Core
import org.koin.dsl.module

fun provideBot() = Core()

val botModule = module {
    single { provideBot() }
}
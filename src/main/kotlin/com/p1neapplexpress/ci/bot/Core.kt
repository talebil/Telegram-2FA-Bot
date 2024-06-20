package com.p1neapplexpress.ci.bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.handlers.ErrorHandler
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.telegramError
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.p1neapplexpress.ci.AlwaysExecuteBotFeature
import com.p1neapplexpress.ci.BotFeature
import com.p1neapplexpress.ci.Storage
import com.p1neapplexpress.ci.feature.AbstractFeature
import com.p1neapplexpress.ci.util.Log
import com.p1neapplexpress.ci.util.ReflectionUtils
import java.util.concurrent.Executors

private const val THREAD_POOL_SIZE = 16

@Suppress("UNCHECKED_CAST")
class Core {

    lateinit var bot: Bot
    private val loadedFeatures by lazy { ReflectionUtils.searchForClassesWithAnnotation(BotFeature::class.java) }
    private val loadedAlwaysExecuteFeatures by lazy { ReflectionUtils.searchForClassesWithAnnotation(AlwaysExecuteBotFeature::class.java) }
    private val threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

    fun initBot() {
        val apiKey = try {
            Storage.getToken()
        } catch (e: Exception) {
            Log.exitError("API key needed: ${e.message}")
            return
        }

        bot = bot {
            token = apiKey
            dispatch {
                addErrorHandler(ErrorHandler {
                    Log.d(this.error.getErrorMessage())
                })
                telegramError {
                    Log.d(this.error.getErrorMessage())
                }
                callbackQuery {
                    triggerFeatures(
                        message = callbackQuery.message ?: return@callbackQuery,
                        update = update,
                        callbackQuery = callbackQuery
                    )
                }
                message {
                    triggerFeatures(
                        message = message,
                        update = update
                    )
                }
            }
        }
    }

    fun startBot() {
        Log.d("startBot()")
        newThread { bot.startPolling() }
    }

    private fun triggerFeatures(message: Message, update: Update, callbackQuery: CallbackQuery? = null) {
        loadedAlwaysExecuteFeatures.forEach {
            val interrupt = runFeature((it as Class<out AbstractFeature>), message, update, callbackQuery)
            if (interrupt) return
        }

        val valueRequests = FeatureValueRequestsStore.requests
        if (valueRequests.isNotEmpty()) {
            val valReq = valueRequests.pop()
            FeatureMemoryStore.get(valReq.requestingFeature.userId)?.apply {
                put(valReq.key ?: return@apply, message.text ?: return@apply)
            }
            valReq.requestingFeature.execute()
            return
        }

        loadedFeatures.forEach {
            val interrupt = runFeature((it as Class<out AbstractFeature>), message, update, callbackQuery)
            if (interrupt) return
        }
    }

    private fun runFeature(it: Class<out AbstractFeature>, message: Message, update: Update, callbackQuery: CallbackQuery?): Boolean {
        val feature = it.getDeclaredConstructor(Message::class.java, Bot::class.java, Update::class.java).newInstance(message, bot, update)
        feature.callbackQuery = callbackQuery

        if (feature.available()) {
            Log.d("[${feature.userId}] Feature ${feature.javaClass.simpleName} ...")

            FeatureMemoryStore.allocate(feature.userId)
            newThread { feature.execute() }
            if (feature.interrupt) return true
        }

        return false
    }

    private fun newThread(function: () -> Unit) {
        threadPool.execute(function)
    }

}
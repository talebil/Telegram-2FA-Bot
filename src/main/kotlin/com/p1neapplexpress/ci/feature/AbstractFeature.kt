package com.p1neapplexpress.ci.feature

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*
import com.p1neapplexpress.ci.Resources
import com.p1neapplexpress.ci.Storage
import com.p1neapplexpress.ci.bot.FeatureMemoryStore
import com.p1neapplexpress.ci.bot.FeatureValueRequestsStore
import com.p1neapplexpress.ci.bot.ValueRequest
import com.p1neapplexpress.ci.util.LocaleManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

abstract class AbstractFeature(
    val message: Message,
    val bot: Bot,
    val update: Update
) : KoinComponent {
    val localeManager: LocaleManager = get()
    val userId: Long get() = message.chat.id
    var callbackQuery: CallbackQuery? = null
    open val interrupt: Boolean = false

    abstract fun available(): Boolean
    abstract fun execute()

    open fun getActions(): ReplyMarkup {
        return KeyboardReplyMarkup.createSimpleKeyboard(
            keyboard = listOf(
                listOf(
                    res(Resources.RES_SERVICES_LIST),
                    res(Resources.RES_ADD_SERVICE),
                ), listOf(
                    res(Resources.RES_SETTINGS)
                )
            )
        )
    }

    fun res(key: String): String = localeManager.getLocaleEntry(locale(), key)

    fun mem() = FeatureMemoryStore.get(userId)

    fun send(
        message: String? = null, messageRes: String? = null, locale: String = locale(),
        replyMarkup: ReplyMarkup? = null, parseMode: ParseMode? = ParseMode.MARKDOWN
    ) {
        if (messageRes != null) {
            send(
                message = res(messageRes),
                replyMarkup = replyMarkup
            )
        }
        if (message != null) bot.sendMessage(
            ChatId.fromId(userId),
            text = message,
            replyMarkup = replyMarkup,
            parseMode = parseMode
        )
    }

    fun requestValue(key: String? = null, feature: AbstractFeature? = null) {
        FeatureValueRequestsStore.requests.push(
            ValueRequest(feature ?: this, key)
        )

        bot.deleteMessage(ChatId.fromId(userId), messageId = message.messageId)
    }

    private fun locale(): String {
        fun fromDatabase() = Storage.userQueries.getLocale(userId).executeAsOneOrNull() ?: localeManager.getDefaultLocale()
        val memory = mem() ?: return fromDatabase()

        if (MEM_LOCALE_KEY !in memory) {
            memory[MEM_LOCALE_KEY] = fromDatabase()
        }

        return memory[MEM_LOCALE_KEY].toString()
    }

    companion object {
        const val MEM_LOCALE_KEY = "locale"
    }
}
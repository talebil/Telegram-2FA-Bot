package com.p1neapplexpress.ci.feature

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.p1neapplexpress.ci.BotFeature
import com.p1neapplexpress.ci.Resources
import com.p1neapplexpress.ci.Resources.RES_LOCALE_PROMPT_MESSAGE
import com.p1neapplexpress.ci.Storage
import com.p1neapplexpress.ci.data.CallbackQueryData
import com.p1neapplexpress.ci.data.callbackQueryData

@BotFeature
class LocaleFeature(
    message: Message, bot: Bot, update: Update
) : AbstractFeature(message, bot, update) {

    override fun available(): Boolean = message.text == COMMAND || callbackQuery?.callbackQueryData()?.command?.equals(COMMAND) == true

    override fun execute() {
        if (callbackQuery?.callbackQueryData()?.params?.containsKey(LANG_PARAM) == false) {
            val locales = InlineKeyboardMarkup.create(mutableListOf<List<InlineKeyboardButton>>().apply {
                localeManager.getLocales().map { it.lang }.forEach { add(getLocaleItem(it)) }
            })

            send(messageRes = RES_LOCALE_PROMPT_MESSAGE, replyMarkup = locales)
        } else {
            val locale = callbackQuery?.callbackQueryData()?.params?.get(LANG_PARAM)
            Storage.userQueries.updateLocale(locale ?: return, userId)
            mem()?.remove(MEM_LOCALE_KEY)

            send(messageRes = Resources.RES_LOCALE_UPDATED, replyMarkup = getActions())
        }
    }

    private fun getLocaleItem(locale: String): List<InlineKeyboardButton.CallbackData> = listOf(
        InlineKeyboardButton.CallbackData(
            res(locale),
            CallbackQueryData(COMMAND, hashMapOf(LANG_PARAM to locale)).toCallbackQuery()
        )
    )

    private companion object {
        private const val LANG_PARAM = "lang"
        private const val COMMAND = "/locale"
    }

}
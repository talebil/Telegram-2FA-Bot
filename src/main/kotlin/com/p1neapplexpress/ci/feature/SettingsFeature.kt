package com.p1neapplexpress.ci.feature

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.p1neapplexpress.ci.BotFeature
import com.p1neapplexpress.ci.Resources
import com.p1neapplexpress.ci.data.CallbackQueryData

@BotFeature
class SettingsFeature(
    message: Message, bot: Bot, update: Update
) : AbstractFeature(message, bot, update) {

    private val settings = mapOf(
        res(Resources.RES_LANGUAGE) to LOCALE_FEATURE_COMMAND
    )

    override fun available(): Boolean = message.text in listOf(
        COMMAND, res(Resources.RES_SETTINGS)
    )

    override fun execute() {
        val settingsKeyboard = InlineKeyboardMarkup.create(mutableListOf<List<InlineKeyboardButton>>().apply {
            settings.map { add(getItem(it)) }
        })

        send(messageRes = Resources.RES_BOT_SETTINGS, replyMarkup = settingsKeyboard)
    }

    private fun getItem(it: Map.Entry<String, String>) = listOf(
        InlineKeyboardButton.CallbackData(it.key, CallbackQueryData(it.value, hashMapOf()).toCallbackQuery())
    )

    private companion object {
        private const val COMMAND = "/settings"
        private const val LOCALE_FEATURE_COMMAND = "/locale"
    }

}
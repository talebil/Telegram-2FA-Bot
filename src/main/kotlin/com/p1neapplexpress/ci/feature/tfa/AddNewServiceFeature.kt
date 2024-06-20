package com.p1neapplexpress.ci.feature.tfa

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.entities.Update
import com.p1neapplexpress.ci.BotFeature
import com.p1neapplexpress.ci.Resources
import com.p1neapplexpress.ci.Storage
import com.p1neapplexpress.ci.data.callbackQueryData
import com.p1neapplexpress.ci.feature.AbstractFeature
import org.koin.core.component.KoinComponent

@BotFeature
class AddNewServiceFeature(
    message: Message, bot: Bot, update: Update
) : AbstractFeature(message, bot, update), KoinComponent {

    override fun available(): Boolean = message.text in listOf(
        COMMAND, res(Resources.RES_ADD_SERVICE)
    ) || callbackQuery?.callbackQueryData()?.command.equals(COMMAND)

    override fun execute() {
        val memory = mem() ?: return
        val params = callbackQuery?.callbackQueryData()?.params

        if (SERVICE_NAME !in memory) {
            if (callbackQuery == null) {
                requestServiceName()
                return
            } else {
                params?.let { memory[SERVICE_NAME] = params[SERVICE_NAME] ?: return@let }
            }
        }

        if (SERVICE_KEY !in memory) {
            if (callbackQuery == null) {
                requestServiceKey()
                return
            }
        }

        if (memory[SERVICE_KEY].toString().length < 12) {
            send(messageRes = Resources.RES_INVALID_VALUE)
            requestServiceKey()
            return
        }

        val name = memory[SERVICE_NAME].toString()
        val key = memory[SERVICE_KEY].toString()

        Storage.tfaConfigQueries.add(userId, key, name)
        memory.clear()

        send(messageRes = Resources.RES_SUCCESS, replyMarkup = getActions())
    }

    private fun requestServiceKey() {
        send(messageRes = Resources.RES_ADD_SERVICE_KEY_PROMPT, replyMarkup = cancelKeyboard())
        requestValue(SERVICE_KEY)
    }

    private fun requestServiceName() {
        send(messageRes = Resources.RES_ADD_SERVICE_NAME_PROMPT, replyMarkup = cancelKeyboard())
        requestValue(SERVICE_NAME)
    }

    private fun cancelKeyboard(): ReplyMarkup = KeyboardReplyMarkup.createSimpleKeyboard(
        keyboard = listOf(listOf(res(Resources.RES_CANCEL))), oneTimeKeyboard = true
    )

    companion object {
        private const val SERVICE_NAME = "service_name"
        private const val SERVICE_KEY = "service_key"

        private const val COMMAND = "/add"
    }

}

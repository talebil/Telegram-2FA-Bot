package com.p1neapplexpress.ci.feature

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.p1neapplexpress.ci.BotFeature
import com.p1neapplexpress.ci.Resources
import com.p1neapplexpress.ci.Storage

@BotFeature
class StartFeature(
    message: Message, bot: Bot, update: Update
) : AbstractFeature(message, bot, update) {

    override fun available(): Boolean = message.text == COMMAND

    override fun execute() {
        send(messageRes = Resources.RES_START_MESSAGE, replyMarkup = getActions())
        Storage.userQueries.apply {
            if (!containsId(userId).executeAsOne()) createNewIfNotExists(userId, Resources.DEFAULT_LOCALE)
        }
    }

    private companion object {
        private const val COMMAND = "/start"
    }

}
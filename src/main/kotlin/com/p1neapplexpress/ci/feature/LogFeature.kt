package com.p1neapplexpress.ci.feature

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.p1neapplexpress.ci.BotFeature
import com.p1neapplexpress.ci.util.Log

@BotFeature
class LogFeature(
    message: Message, bot: Bot, update: Update
) : AbstractFeature(message, bot, update) {

    override fun available(): Boolean = true
    override val interrupt: Boolean = false

    override fun execute() {
        val name = message.from?.let { "${it.firstName} ${it.lastName ?: "\b"} ${it.username ?: "\b"}" }
        Log.d("[$userId] $name: ${message.text}")
    }

}
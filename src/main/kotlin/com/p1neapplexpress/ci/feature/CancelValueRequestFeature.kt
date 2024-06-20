package com.p1neapplexpress.ci.feature

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.p1neapplexpress.ci.AlwaysExecuteBotFeature
import com.p1neapplexpress.ci.Resources
import com.p1neapplexpress.ci.bot.FeatureValueRequestsStore
import com.p1neapplexpress.ci.data.callbackQueryData

@AlwaysExecuteBotFeature
class CancelValueRequestFeature(
    message: Message, bot: Bot, update: Update
) : AbstractFeature(message, bot, update) {

    override fun available(): Boolean = callbackQuery?.callbackQueryData()?.command?.equals(COMMAND) == true || message.text == res(Resources.RES_CANCEL)
    override val interrupt: Boolean = true

    override fun execute() {
        FeatureValueRequestsStore.requests.clear()
        mem()?.clear()
        send(messageRes = Resources.RES_CANCELED, replyMarkup = getActions())
    }

    private companion object {
        const val COMMAND = "cancel_val_req"
    }
}
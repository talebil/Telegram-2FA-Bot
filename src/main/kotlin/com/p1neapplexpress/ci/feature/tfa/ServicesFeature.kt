package com.p1neapplexpress.ci.feature.tfa

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import com.p1neapplexpress.ci.BotFeature
import com.p1neapplexpress.ci.Resources
import com.p1neapplexpress.ci.Storage
import com.p1neapplexpress.ci.data.callbackQueryData
import com.p1neapplexpress.ci.feature.AbstractFeature
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import org.koin.core.component.KoinComponent

@BotFeature
class ServicesFeature(
    message: Message, bot: Bot, update: Update
) : AbstractFeature(message, bot, update), KoinComponent {

    override fun available(): Boolean = message.text in listOf(
        COMMAND, res(Resources.RES_SERVICES_LIST)
    ) || callbackQuery?.callbackQueryData()?.command.equals(COMMAND)

    override fun execute() {
        val result = StringBuilder()

        val services = Storage.tfaConfigQueries.getByUserId(userId.toString()).executeAsList()
        for (service in services) {
            result.append(
                "[${service.title}]: <code>${getCodeFor(service.key)}</code>\n"
            )
        }

        send("${res(Resources.RES_SERVICES_LIST)}: \n\n\n$result", parseMode = ParseMode.HTML)
    }

    private fun getCodeFor(key: String): String {
        val googleAuthenticator = GoogleAuthenticator(key.toByteArray())
        return googleAuthenticator.generate()
    }

    companion object {
        private const val COMMAND = "/list"
    }

}

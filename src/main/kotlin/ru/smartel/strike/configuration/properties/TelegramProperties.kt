package ru.smartel.strike.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "publications.telegram")
class TelegramProperties{
    /**
     * Chat or channel id (with '@' or '-' prefix)
     */
    var chatId: String = ""
    /**
     * Bot identity
     */
    var botId: String = ""
    /**
     * Bot secret token
     */
    var botToken: String = ""

    val specified
        get() = chatId.isNotBlank() && botId.isNotBlank() && botToken.isNotBlank()
}

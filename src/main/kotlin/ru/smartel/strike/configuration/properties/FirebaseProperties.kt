package ru.smartel.strike.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "firebase")
class FirebaseProperties {
    /**
     * Whether to use auth stub or not (autologin as moderator)
     */
    var authStub: Boolean = false

    /**
     * Base64-decoded service-account json credentials
     * If not set -> messaging and authentication won`t work
     */
    var credentials: String = ""
}

package ru.smartel.strike.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "publications.ok")
class OkProperties{
    /**
     * Application eternal access token
     */
    var accessToken: String = ""
    /**
     * Application secret
     */
    var appSecret: String = ""
    /**
     * Application public key
     */
    var appKey: String = ""
    /**
     * Id of OK group to publish to
     */
    var gid: String = ""

    val specified
        get() = gid.isNotBlank() && appSecret.isNotBlank() && appKey.isNotBlank() && accessToken.isNotBlank()
}

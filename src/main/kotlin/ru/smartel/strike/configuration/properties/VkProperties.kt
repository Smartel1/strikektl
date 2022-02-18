package ru.smartel.strike.configuration.properties

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
@ConfigurationProperties(prefix = "publications.vk")
class VkProperties {
    /**
     *  VK group id (numeric)
     */
    var groupId: String = ""

    /**
     * Application access token
     */
    var accessToken: String = ""

    val specified
        get() = groupId.isNotBlank() && accessToken.isNotBlank()
}

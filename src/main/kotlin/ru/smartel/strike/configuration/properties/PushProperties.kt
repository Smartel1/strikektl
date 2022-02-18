package ru.smartel.strike.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Configuration
@ConfigurationProperties(prefix = "push")
@Validated
class PushProperties {
    /**
     * Whether to use auth stub or not (autologin as moderator)
     */
    @NotNull
    var enabled: Boolean = false

    /**
     * Base64-decoded service-account json credentials
     * If not set -> messaging and authentication won`t work
     */
    @Valid
    var topics: String = ""
}

class Topics {
    @NotBlank
    var admin: String = ""
    var news: LocaleSet? = null
    var events: LocaleSet? = null
}

class LocaleSet {
    @NotBlank
    var ru: String = ""

    @NotBlank
    var en: String = ""

    @NotBlank
    var es: String = ""

    @NotBlank
    var de: String = ""
}

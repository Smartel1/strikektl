package ru.smartel.strike.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Configuration
@ConfigurationProperties(prefix = "push")
@Validated
class PushProperties {
    /**
     * Whether to use auth stub or not (autologin as moderator)
     */
    var enabled: Boolean = false

    @Valid
    var topics: Topics = Topics()
}

class Topics {
    @NotBlank
    var admin: String = ""
    var news: LocaleSet = LocaleSet()
    var events: LocaleSet = LocaleSet()
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

package ru.smartel.strike.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import ru.smartel.strike.service.Locale

@Component
class LocaleConverter : Converter<String, Locale> {
    override fun convert(locale: String) = Locale.valueOf(locale.uppercase())
}
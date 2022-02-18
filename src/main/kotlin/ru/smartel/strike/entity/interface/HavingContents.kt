package ru.smartel.strike.entity.`interface`

import ru.smartel.strike.service.Locale

interface HavingContents {
    var contentRu: String?
    var contentEn: String?
    var contentEs: String?
    var contentDe: String?

    fun getContentByLocale(locale: Locale): String? {
        return when (locale) {
            Locale.RU -> contentRu
            Locale.EN -> contentEn
            Locale.ES -> contentEs
            Locale.DE -> contentDe
            else -> ""
        }
    }

    fun setContentByLocale(locale: Locale, content: String) {
        when (locale) {
            Locale.RU -> contentRu = content
            Locale.EN -> contentEn = content
            Locale.ES -> contentEs = content
            Locale.DE -> contentDe = content
            else -> {}
        }
    }
}
package ru.smartel.strike.entity.interfaces

import ru.smartel.strike.service.Locale

interface HavingTitles {
    var titleRu: String?
    var titleEn: String?
    var titleEs: String?
    var titleDe: String?

    fun getTitleByLocale(locale: Locale): String? {
        return when (locale) {
            Locale.RU -> titleRu
            Locale.EN -> titleEn
            Locale.ES -> titleEs
            Locale.DE -> titleDe
            else -> ""
        }
    }

    fun setTitleByLocale(locale: Locale, title: String?) {
        when (locale) {
            Locale.RU -> titleRu = title
            Locale.EN -> titleEn = title
            Locale.ES -> titleEs = title
            Locale.DE -> titleDe = title
            else -> {}
        }
    }
}
package ru.smartel.strike.entity.`interface`

import ru.smartel.strike.service.Locale

interface HavingNames {
    var nameRu: String?
    var nameEn: String?
    var nameEs: String?
    var nameDe: String?

    fun getNameByLocale(locale: Locale): String? {
        return when (locale) {
            Locale.RU -> nameRu
            Locale.EN -> nameEn
            Locale.ES -> nameEs
            Locale.DE -> nameDe
            else -> ""
        }
    }

    fun setNameByLocale(locale: Locale, name: String) {
        when (locale) {
            Locale.RU -> nameRu = name
            Locale.EN -> nameEn = name
            Locale.ES -> nameEs = name
            Locale.DE -> nameDe = name
            else -> {}
        }
    }
}
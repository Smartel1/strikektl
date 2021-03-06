package ru.smartel.strike.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.interfaces.HavingTitles
import ru.smartel.strike.service.Locale

data class TitlesDto(
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val title: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val titleRu: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val titleEn: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val titleEs: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val titleDe: String? = IGNORE,
) {
    constructor(entity: HavingTitles, locale: Locale) : this(
        title = if (locale != Locale.ALL) entity.getTitleByLocale(locale) else IGNORE,
        titleRu = if (locale == Locale.ALL) entity.titleRu else IGNORE,
        titleEn = if (locale == Locale.ALL) entity.titleEn else IGNORE,
        titleEs = if (locale == Locale.ALL) entity.titleEs else IGNORE,
        titleDe = if (locale == Locale.ALL) entity.titleDe else IGNORE
    )

    companion object {
        fun withDefaultRuLocale(entity: HavingTitles, locale: Locale): TitlesDto {
            /**
             * If title is not localized use default (Ru)
             * Never contains null. Maybe its not good
             */
            fun getByLocaleOrRuOrEmpty(entity: HavingTitles, locale: Locale): String {
                val byLocale = entity.getTitleByLocale(locale)
                if (!byLocale.isNullOrBlank()) return byLocale
                if (entity.titleRu != null) return entity.titleRu!!
                return ""
            }

            return TitlesDto(
                title = if (locale != Locale.ALL) getByLocaleOrRuOrEmpty(entity, locale) else IGNORE,
                titleRu = if (locale == Locale.ALL) entity.titleRu else IGNORE,
                titleEn = if (locale == Locale.ALL) entity.titleEn else IGNORE,
                titleEs = if (locale == Locale.ALL) entity.titleEs else IGNORE,
                titleDe = if (locale == Locale.ALL) entity.titleDe else IGNORE
            )
        }
    }
}

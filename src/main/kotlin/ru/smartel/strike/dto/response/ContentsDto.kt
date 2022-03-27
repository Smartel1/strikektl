package ru.smartel.strike.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.interfaces.HavingContents
import ru.smartel.strike.service.Locale

data class ContentsDto(
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val content: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val contentRu: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val contentEn: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val contentEs: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val contentDe: String? = IGNORE,
) {
    constructor(entity: HavingContents, locale: Locale) : this(
        content = if (locale != Locale.ALL) entity.getContentByLocale(locale) else IGNORE,
        contentRu = if (locale == Locale.ALL) entity.contentRu else IGNORE,
        contentEn = if (locale == Locale.ALL) entity.contentEn else IGNORE,
        contentEs = if (locale == Locale.ALL) entity.contentEs else IGNORE,
        contentDe = if (locale == Locale.ALL) entity.contentDe else IGNORE
    )
}

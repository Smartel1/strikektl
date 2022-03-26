package ru.smartel.strike.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.`interface`.HavingNames
import ru.smartel.strike.service.Locale

data class NamesDto(
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val name: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val nameRu: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val nameEn: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val nameEs: String? = IGNORE,
    @field:JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = JsonDefaultStringFilter::class)
    val nameDe: String? = IGNORE,
) {
    constructor(entity: HavingNames, locale: Locale) : this(
        name = if (locale != Locale.ALL) entity.getNameByLocale(locale) else IGNORE,
        nameRu = if (locale == Locale.ALL) entity.nameRu else IGNORE,
        nameEn = if (locale == Locale.ALL) entity.nameEn else IGNORE,
        nameEs = if (locale == Locale.ALL) entity.nameEs else IGNORE,
        nameDe = if (locale == Locale.ALL) entity.nameDe else IGNORE
    )
}

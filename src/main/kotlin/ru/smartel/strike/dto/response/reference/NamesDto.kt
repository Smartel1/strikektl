package ru.smartel.strike.dto.response.reference

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.`interface`.HavingNames
import ru.smartel.strike.service.Locale

data class NamesDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String?,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val nameRu: String?,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val nameEn: String?,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val nameEs: String?,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val nameDe: String?,
) {
    constructor(entity: HavingNames, locale: Locale) : this(
        name = if (locale != Locale.ALL) entity.getNameByLocale(locale) else null,
        nameRu = if (locale == Locale.ALL) entity.nameRu else null,
        nameEn = if (locale == Locale.ALL) entity.nameEn else null,
        nameEs = if (locale == Locale.ALL) entity.nameEs else null,
        nameDe = if (locale == Locale.ALL) entity.nameDe else null
    )
}

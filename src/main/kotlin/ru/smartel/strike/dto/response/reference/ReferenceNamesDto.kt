package ru.smartel.strike.dto.response.reference

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.reference.EntityWithNames
import ru.smartel.strike.service.Locale

data class ReferenceNamesDto(
    val id: Long,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val nameRu: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val nameEn: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val nameEs: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val nameDe: String?
) {
    constructor(entity: EntityWithNames, locale: Locale) : this(
        entity.id,
        name = if (locale != Locale.ALL) entity.getNameByLocale(locale) else null,
        nameRu = if (locale == Locale.ALL) entity.nameRu else null,
        nameEn = if (locale == Locale.ALL) entity.nameEn else null,
        nameEs = if (locale == Locale.ALL) entity.nameEs else null,
        nameDe = if (locale == Locale.ALL) entity.nameDe else null
    )
}

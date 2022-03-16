package ru.smartel.strike.dto.response.reference.locality

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.reference.LocalityEntity
import ru.smartel.strike.service.Locale

data class LocalityDetailDto(
    val id: Long,
    val name: String,
    val region: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val country: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val countryRu: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val countryEn: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val countryEs: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val countryDe: String?
) {
    constructor(entity: LocalityEntity, locale: Locale) : this(
        entity.id, entity.name, entity.region.name,
        country = if (locale != Locale.ALL) entity.region.country.getNameByLocale(locale) else null,
        countryRu = if (locale == Locale.ALL) entity.region.country.nameRu else null,
        countryEn = if (locale == Locale.ALL) entity.region.country.nameEn else null,
        countryEs = if (locale == Locale.ALL) entity.region.country.nameEs else null,
        countryDe = if (locale == Locale.ALL) entity.region.country.nameDe else null
    )
}
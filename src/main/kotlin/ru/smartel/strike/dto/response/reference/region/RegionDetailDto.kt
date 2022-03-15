package ru.smartel.strike.dto.response.reference.region

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.reference.RegionEntity
import ru.smartel.strike.service.Locale

data class RegionDetailDto(
    val id: Long,
    val name: String,
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
    constructor(entity: RegionEntity, locale: Locale) : this(
        entity.id, entity.name,
        country = if (locale != Locale.ALL) entity.country.getNameByLocale(locale) else null,
        countryRu = if (locale == Locale.ALL) entity.country.nameRu else null,
        countryEn = if (locale == Locale.ALL) entity.country.nameEn else null,
        countryEs = if (locale == Locale.ALL) entity.country.nameEs else null,
        countryDe = if (locale == Locale.ALL) entity.country.nameDe else null
    )
}
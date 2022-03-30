package ru.smartel.strike.dto.response.reference.locality

import ru.smartel.strike.dto.response.reference.country.CountryDetailDto
import ru.smartel.strike.dto.response.reference.region.RegionDto
import ru.smartel.strike.entity.reference.LocalityEntity
import ru.smartel.strike.service.Locale

data class LocalityDto(
    val id: Long,
    val name: String,
    val region: RegionDto
) {
    constructor(locality: LocalityEntity, locale: Locale) : this(
        id = locality.id,
        name = locality.name,
        region = RegionDto(
            id = locality.region.id,
            name = locality.region.name,
            country = CountryDetailDto(
                entity = locality.region.country,
                locale = locale
            )
        )
    )
}
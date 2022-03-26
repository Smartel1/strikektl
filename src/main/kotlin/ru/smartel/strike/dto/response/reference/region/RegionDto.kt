package ru.smartel.strike.dto.response.reference.region

import ru.smartel.strike.dto.response.reference.country.CountryDetailDto

data class RegionDto(
    val id: Long,
    val name: String,
    val country: CountryDetailDto,
)
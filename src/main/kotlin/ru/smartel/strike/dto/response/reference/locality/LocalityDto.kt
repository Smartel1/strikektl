package ru.smartel.strike.dto.response.reference.locality

import ru.smartel.strike.dto.response.reference.region.RegionDto

data class LocalityDto(
    val id: Long,
    val name: String,
    val region: RegionDto
)
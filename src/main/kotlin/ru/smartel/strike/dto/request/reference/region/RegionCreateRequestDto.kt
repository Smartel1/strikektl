package ru.smartel.strike.dto.request.reference.region

import com.fasterxml.jackson.annotation.JsonIgnore
import ru.smartel.strike.service.Locale

data class RegionCreateRequestDto(
    @JsonIgnore
    var locale: Locale?,
    val name: String?,
    val countryId: Long?
)
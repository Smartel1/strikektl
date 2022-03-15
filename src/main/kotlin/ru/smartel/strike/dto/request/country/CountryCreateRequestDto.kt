package ru.smartel.strike.dto.request.country

import com.fasterxml.jackson.annotation.JsonIgnore
import ru.smartel.strike.service.Locale

data class CountryCreateRequestDto(
    @JsonIgnore
    var locale: Locale?,
    val nameRu: String?,
    val nameEn: String?,
    val nameEs: String?,
    val nameDe: String?
)
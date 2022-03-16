package ru.smartel.strike.dto.request.reference.locality

import com.fasterxml.jackson.annotation.JsonIgnore
import ru.smartel.strike.service.Locale

data class LocalityCreateRequestDto(
    @JsonIgnore
    var locale: Locale?,
    val name: String?,
    val regionId: Long?
)
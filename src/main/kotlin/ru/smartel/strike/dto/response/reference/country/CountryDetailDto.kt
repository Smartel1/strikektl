package ru.smartel.strike.dto.response.reference.country

import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.reference.NamesDto
import ru.smartel.strike.entity.reference.CountryEntity
import ru.smartel.strike.service.Locale

data class CountryDetailDto(
    val id: Long,
    @JsonUnwrapped
    val names: NamesDto,
) {
    constructor(entity: CountryEntity, locale: Locale) :
            this(entity.id, NamesDto(entity, locale))
}
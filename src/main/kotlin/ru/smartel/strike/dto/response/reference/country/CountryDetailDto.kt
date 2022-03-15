package ru.smartel.strike.dto.response.reference.country

import ru.smartel.strike.dto.response.reference.NamesExtendableDto
import ru.smartel.strike.entity.reference.CountryEntity
import ru.smartel.strike.service.Locale

data class CountryDetailDto(
    val id: Long,
) : NamesExtendableDto() {
    constructor(entity: CountryEntity, locale: Locale) : this(entity.id) {
        setNamesOf(entity, locale)
    }
}
package ru.smartel.strike.dto.response.reference

import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.NamesDto
import ru.smartel.strike.entity.reference.EntityWithNames
import ru.smartel.strike.service.Locale

data class ReferenceNamesDto(
    val id: Long,
    @JsonUnwrapped
    val names: NamesDto,
) {
    constructor(entity: EntityWithNames, locale: Locale) :
            this(entity.id, NamesDto(entity, locale))
}

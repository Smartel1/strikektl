package ru.smartel.strike.dto.response.reference

import ru.smartel.strike.entity.reference.EntityWithNames
import ru.smartel.strike.service.Locale

data class ReferenceNamesDto(
    val id: Long,
) : NamesExtendableDto() {
    constructor(entity: EntityWithNames, locale: Locale) : this(entity.id) {
        setNamesOf(entity, locale)
    }
}

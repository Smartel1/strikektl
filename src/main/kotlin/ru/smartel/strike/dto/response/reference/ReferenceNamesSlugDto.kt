package ru.smartel.strike.dto.response.reference

import ru.smartel.strike.entity.reference.EntityWithNamesAndSlug
import ru.smartel.strike.service.Locale

data class ReferenceNamesSlugDto(
    val id: Long,
    val slug: String,
) : NamesExtendableDto() {
    constructor(entity: EntityWithNamesAndSlug, locale: Locale) : this(entity.id, entity.slug) {
        setNamesOf(entity, locale)
    }
}

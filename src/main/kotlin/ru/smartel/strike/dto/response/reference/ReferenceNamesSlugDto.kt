package ru.smartel.strike.dto.response.reference

import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.NamesDto
import ru.smartel.strike.entity.reference.EntityWithNamesAndSlug
import ru.smartel.strike.service.Locale

data class ReferenceNamesSlugDto(
    val id: Long,
    val slug: String,
    @JsonUnwrapped
    val names: NamesDto,
) {
    constructor(entity: EntityWithNamesAndSlug, locale: Locale) :
            this(entity.id, entity.slug, NamesDto(entity, locale))
}

package ru.smartel.strike.dto.response.client_version

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.ClientVersionEntity
import ru.smartel.strike.service.Locale

data class ClientVersionDto(
    val id: Long,
    val version: String,
    val isRequired: Boolean,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val description: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val descriptionRu: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val descriptionEn: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val descriptionEs: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val descriptionDe: String?
) {
    constructor(entity: ClientVersionEntity, locale: Locale) : this(
        entity.id, entity.version, entity.required,
        description = if (locale != Locale.ALL) entity.getDescriptionByLocale(locale) else null,
        descriptionRu = if (locale == Locale.ALL) entity.descriptionRu else null,
        descriptionEn = if (locale == Locale.ALL) entity.descriptionEn else null,
        descriptionEs = if (locale == Locale.ALL) entity.descriptionEs else null,
        descriptionDe = if (locale == Locale.ALL) entity.descriptionDe else null
    )
}
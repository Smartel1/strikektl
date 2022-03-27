package ru.smartel.strike.dto.response.conflict

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.TitlesDto
import ru.smartel.strike.entity.ConflictEntity
import ru.smartel.strike.service.Locale

data class ConflictDetailDto(
    val id: Long,
    @JsonUnwrapped
    val titles: TitlesDto,
    @JsonUnwrapped
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val fullConflictDto: FullConflictDto,
    val mainTypeId: Long?,
    val automanagingMainType: Boolean
) {
    constructor(entity: ConflictEntity, locale: Locale) : this(
        id = entity.id,
        titles = TitlesDto(entity, locale),
        fullConflictDto = FullConflictDto(entity),
        mainTypeId = entity.mainType?.id,
        automanagingMainType = entity.automanagingMainType
    )
}
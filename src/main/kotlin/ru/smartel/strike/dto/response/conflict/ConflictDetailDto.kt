package ru.smartel.strike.dto.response.conflict

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.TitlesDto

data class ConflictDetailDto(
    val id: Long,
    @JsonUnwrapped
    val titles: TitlesDto,
    @JsonUnwrapped
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val fullConflictDto: FullConflictDto,
    val mainTypeId: Long?,
    val automanagingMainType: Boolean
)
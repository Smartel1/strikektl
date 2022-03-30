package ru.smartel.strike.dto.response.event

import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.TitlesDto

data class BriefEventDto(
    var id: Long,
    val date: Long,
    @JsonUnwrapped
    val titles: TitlesDto
)

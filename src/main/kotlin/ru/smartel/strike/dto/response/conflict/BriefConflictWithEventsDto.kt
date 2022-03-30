package ru.smartel.strike.dto.response.conflict

import ru.smartel.strike.dto.response.event.BriefEventDto

data class BriefConflictWithEventsDto(
    val id: Long,
    val parentEventId: Long?,
    val parentConflictId: Long?,
    val events: List<BriefEventDto>,
)
package ru.smartel.strike.dto.response.event

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.conflict.BriefConflictWithEventsDto
import ru.smartel.strike.dto.response.conflict.ConflictDetailDto
import ru.smartel.strike.dto.response.post.PostDetailDto
import ru.smartel.strike.dto.response.reference.locality.LocalityDto
import ru.smartel.strike.entity.EventEntity
import ru.smartel.strike.service.Locale
import java.time.ZoneOffset

data class EventDetailDto(
    var latitude: Float,
    val longitude: Float,
    val conflictId: Long,
    val eventStatusId: Long,
    val eventTypeId: Long?,
    val createdAt: Long,
    val conflict: ConflictDetailDto,
    val locality: LocalityDto?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val relatives: List<BriefConflictWithEventsDto>?,
    @JsonUnwrapped
    val postDetails: PostDetailDto
) {
    constructor(event: EventEntity, locale: Locale, relatives: List<BriefConflictWithEventsDto>? = null) : this(
        latitude = event.latitude,
        longitude = event.longitude,
        conflictId = event.conflict!!.id,
        eventStatusId = event.status!!.id,
        eventTypeId = event.type?.id,
        createdAt = event.createdAt!!.toEpochSecond(ZoneOffset.UTC),
        conflict = ConflictDetailDto(event.conflict!!, locale),
        locality = event.locality?.let { LocalityDto(it, locale) },
        relatives = relatives,
        postDetails = PostDetailDto(event, locale)
    )
}

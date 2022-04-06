package ru.smartel.strike.dto.response.event

import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.conflict.ConflictDetailDto
import ru.smartel.strike.dto.response.post.PostListDto
import ru.smartel.strike.entity.EventEntity
import ru.smartel.strike.service.Locale
import java.time.ZoneOffset

data class EventListDto(
    var latitude: Float,
    val longitude: Float,
    val conflictId: Long,
    val eventStatusId: Long,
    val eventTypeId: Long?,
    val createdAt: Long,
    val conflict: ConflictDetailDto,
    @JsonUnwrapped
    val post: PostListDto
) {
    constructor(event: EventEntity, locale: Locale) : this(
        latitude = event.latitude,
        longitude = event.longitude,
        conflictId = event.conflict.id,
        eventStatusId = event.status!!.id,
        eventTypeId = event.type?.id,
        createdAt = event.createdAt!!.toEpochSecond(ZoneOffset.UTC),
        conflict = ConflictDetailDto(event.conflict, locale),
        post = PostListDto(event, locale)
    )
}

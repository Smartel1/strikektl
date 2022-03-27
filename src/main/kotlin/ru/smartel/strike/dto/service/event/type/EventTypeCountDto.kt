package ru.smartel.strike.dto.service.event.type

data class EventTypeCountDto(
    val typeId: Long,
    val eventsCount: Long,
    val priority: Int
)

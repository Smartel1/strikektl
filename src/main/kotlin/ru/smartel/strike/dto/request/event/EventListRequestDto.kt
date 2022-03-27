package ru.smartel.strike.dto.request.event

data class EventListRequestDto(
    var filters: EventFiltersDto? = EventFiltersDto()
)

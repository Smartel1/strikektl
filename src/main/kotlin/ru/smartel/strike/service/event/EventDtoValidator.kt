package ru.smartel.strike.service.event

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.event.EventListRequestDto
import ru.smartel.strike.util.addErrorIf
import ru.smartel.strike.util.notNull
import ru.smartel.strike.util.throwIfFail

@Component
class EventDtoValidator {
    fun validateListRequestDto(dto: EventListRequestDto) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("filters.near.lat", notNull()) {
                dto.filters?.near?.let { it.lat == null } ?: false
            }
            .addErrorIf("filters.near.lng", notNull()) {
                dto.filters?.near?.let { it.lng == null } ?: false
            }
            .addErrorIf("filters.near.radius", notNull()) {
                dto.filters?.near?.let { it.radius == null } ?: false
            }
            .throwIfFail()
    }
}
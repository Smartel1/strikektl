package ru.smartel.strike.service.event

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.event.EventCreateRequestDto
import ru.smartel.strike.dto.request.event.EventListRequestDto
import ru.smartel.strike.service.post.BasePostDtoValidator
import ru.smartel.strike.util.addErrorIf
import ru.smartel.strike.util.notNull
import ru.smartel.strike.util.required
import ru.smartel.strike.util.throwIfFail

@Component
class EventDtoValidator: BasePostDtoValidator() {
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

    fun validateStoreDto(dto: EventCreateRequestDto) {
        super.validateStoreDto(dto)
            .addErrorIf("conflictId", required()) { dto.conflictId == null }
            .addErrorIf("conflictId", notNull()) { dto.conflictId?.isEmpty == true }
            .addErrorIf("latitude", required()) { dto.latitude == null }
            .addErrorIf("latitude", notNull()) { dto.latitude?.isEmpty == true }
            .addErrorIf("longitude", required()) { dto.longitude == null }
            .addErrorIf("longitude", notNull()) { dto.longitude?.isEmpty == true }
            .throwIfFail()
    }
}
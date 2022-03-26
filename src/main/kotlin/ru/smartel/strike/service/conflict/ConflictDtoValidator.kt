package ru.smartel.strike.service.conflict

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.conflict.ConflictListRequestDto
import ru.smartel.strike.util.*

@Component
class ConflictDtoValidator {
    fun validateListQueryDTO(dto: ConflictListRequestDto) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("locale", notNull()) { dto.locale == null }
            .addErrorIf("filters.near.lat", notNull()) {
                dto.filters?.near?.let { it.lat == null } ?: false
            }
            .addErrorIf("filters.near.lng", notNull()) {
                dto.filters?.near?.let { it.lng == null } ?: false
            }
            .addErrorIf("filters.near.radius", notNull()) {
                dto.filters?.near?.let { it.radius == null } ?: false
            }
            .addErrorIf("filters.mainTypeIds", numericCollection()) {
                dto.filters?.mainTypeIds?.let {
                    it.filter { typeIdString -> typeIdString != "null" }
                        .any { typeIdString -> typeIdString.toLongOrNull() == null }
                } ?: false
            }
            .throwIfFail()
    }
}
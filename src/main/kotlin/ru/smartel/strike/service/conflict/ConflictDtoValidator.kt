package ru.smartel.strike.service.conflict

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.conflict.ConflictListRequestDto
import ru.smartel.strike.util.*

@Component
class ConflictDtoValidator {
    fun validateListQueryDTO(dto: ConflictListRequestDto) {
        val availableSortFields = listOf("createdAt")
        val availableSortOrders = listOf("asc", "desc")
        HashMap<String, ArrayList<String>>()
            .addErrorIf("locale", notNull()) { dto.locale == null }
            .addErrorIf("page", min(1)) { dto.baseListFields.page < 1 }
            .addErrorIf("perPage", min(1)) { dto.baseListFields.perPage < 1 }
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
            .addErrorIf("sort.field", notNull()) {
                dto.baseListFields.sort?.let { it.field == null } ?: false
            }
            .addErrorIf("sort.field", oneOf(availableSortFields)) {
                dto.baseListFields.sort?.field?.let { !availableSortFields.contains(it) } ?: false
            }
            .addErrorIf("sort.order", notNull()) {
                dto.baseListFields.sort?.let { it.order == null } ?: false
            }
            .addErrorIf("sort.order", oneOf(availableSortOrders)) {
                dto.baseListFields.sort?.order?.let { !availableSortOrders.contains(it) } ?: false
            }
            .throwIfFail()
    }
}
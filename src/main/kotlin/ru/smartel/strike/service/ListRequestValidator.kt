package ru.smartel.strike.service

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.util.*

@Component
class ListRequestValidator {
    fun validateListQueryDTO(dto: BaseListRequestDto,
                             availableSortFields: List<String> = listOf("createdAt"),
                             availableSortOrders: List<String> = listOf("asc", "desc")) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("page", min(1)) { dto.page < 1 }
            .addErrorIf("perPage", min(1)) { dto.perPage < 1 }
            .addErrorIf("sort.field", notNull()) {
                dto.sort?.let { it.field == null } ?: false
            }
            .addErrorIf("sort.field", oneOf(availableSortFields)) {
                dto.sort?.field?.let { !availableSortFields.contains(it) } ?: false
            }
            .addErrorIf("sort.order", notNull()) {
                dto.sort?.let { it.order == null } ?: false
            }
            .addErrorIf("sort.order", oneOf(availableSortOrders)) {
                dto.sort?.order?.let { !availableSortOrders.contains(it) } ?: false
            }
            .throwIfFail()
    }
}
package ru.smartel.strike.service.client_version

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.client_version.ClientVersionCreateRequestDto
import ru.smartel.strike.dto.request.client_version.ClientVersionGetNewRequestDto
import ru.smartel.strike.util.addErrorIf
import ru.smartel.strike.util.notNull
import ru.smartel.strike.util.throwIfFail

@Component
class ClientVersionDtoValidator {
    fun validateListRequestDto(request: ClientVersionGetNewRequestDto) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("clientId", notNull()) { request.clientId == null }
            .throwIfFail()
    }

    fun validateStoreDto(request: ClientVersionCreateRequestDto) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("clientId", notNull()) { request.clientId == null }
            .addErrorIf("version", notNull()) { request.version == null }
            .addErrorIf("required", notNull()) { request.required == null }
            .addErrorIf("descriptionRu", notNull()) { request.descriptionRu == null }
            .addErrorIf("descriptionEn", notNull()) { request.descriptionEn == null }
            .addErrorIf("descriptionEs", notNull()) { request.descriptionEs == null }
            .addErrorIf("descriptionDe", notNull()) { request.descriptionDe == null }
            .throwIfFail()
    }
}
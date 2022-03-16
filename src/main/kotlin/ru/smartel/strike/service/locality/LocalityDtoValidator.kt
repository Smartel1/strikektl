package ru.smartel.strike.service.locality

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.reference.locality.LocalityCreateRequestDto
import ru.smartel.strike.util.addErrorIf
import ru.smartel.strike.util.min
import ru.smartel.strike.util.notNull
import ru.smartel.strike.util.throwIfFail

@Component
class LocalityDtoValidator {
    fun validateUpdateDTO(request: LocalityCreateRequestDto) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("locale", notNull()) { request.locale == null }
            .addErrorIf("regionId", notNull()) { request.regionId == null }
            .addErrorIf("name", notNull()) { request.name == null }
            .addErrorIf("name", min(1)) { request.name != null && request.name.isEmpty() }
            .throwIfFail()
    }
}
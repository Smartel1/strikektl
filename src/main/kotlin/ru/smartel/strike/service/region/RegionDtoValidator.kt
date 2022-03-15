package ru.smartel.strike.service.region

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.reference.region.RegionCreateRequestDto
import ru.smartel.strike.util.addErrorIf
import ru.smartel.strike.util.min
import ru.smartel.strike.util.notNull
import ru.smartel.strike.util.throwIfFail

@Component
class RegionDtoValidator {
    fun validateUpdateDTO(request: RegionCreateRequestDto) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("locale", notNull()) { request.locale == null }
            .addErrorIf("countryId", notNull()) { request.countryId == null }
            .addErrorIf("name", notNull()) { request.name == null }
            .addErrorIf("name", min(1)) { request.name != null && request.name.isEmpty() }
            .throwIfFail()
    }
}
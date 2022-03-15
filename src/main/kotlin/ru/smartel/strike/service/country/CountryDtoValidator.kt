package ru.smartel.strike.service.country

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.country.CountryCreateRequestDto
import ru.smartel.strike.util.addErrorIf
import ru.smartel.strike.util.min
import ru.smartel.strike.util.notNull
import ru.smartel.strike.util.throwIfFail

@Component
class CountryDtoValidator {
    fun validateUpdateDTO(request: CountryCreateRequestDto) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("locale", notNull()) { request.locale == null }
            .addErrorIf("nameRu", notNull()) { request.nameRu == null }
            .addErrorIf("nameRu", min(1)) { request.nameRu != null && request.nameRu.isEmpty() }
            .addErrorIf("nameEn", notNull()) { request.nameEn == null }
            .addErrorIf("nameEn", min(1)) { request.nameEn != null && request.nameEn.isEmpty() }
            .addErrorIf("nameEs", notNull()) { request.nameEs == null }
            .addErrorIf("nameEs", min(1)) { request.nameEs != null && request.nameEs.isEmpty() }
            .addErrorIf("nameDe", notNull()) { request.nameDe == null }
            .addErrorIf("nameDe", min(1)) { request.nameDe != null && request.nameDe.isEmpty() }
            .throwIfFail()
    }
}
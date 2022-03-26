package ru.smartel.strike.service.conflict

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.conflict.ConflictFieldsDto
import ru.smartel.strike.dto.request.conflict.ConflictListRequestDto
import ru.smartel.strike.util.*

@Component
class ConflictDtoValidator {
    fun validateListRequestDto(dto: ConflictListRequestDto) {
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
            .addErrorIf("filters.mainTypeIds", numericCollection()) {
                dto.filters?.mainTypeIds?.let {
                    it.filter { typeIdString -> typeIdString != "null" }
                        .any { typeIdString -> typeIdString.toLongOrNull() == null }
                } ?: false
            }
            .throwIfFail()
    }

    fun validateConflictFieldsDtoForCreate(dto: ConflictFieldsDto) {
        validateMandatoryFields(dto)
            .addErrorIf("latitude", notNull()) { dto.latitude == null }
            .addErrorIf("longitude", notNull()) { dto.longitude == null }
            .addErrorIf("conflictReasonId", required()) { dto.conflictReasonId == null }
            .addErrorIf("conflictReasonId", notNull()) { dto.conflictReasonId?.isEmpty ?: false }
            .throwIfFail()
    }

    fun validateConflictFieldsDtoForUpdate(dto: ConflictFieldsDto) {
        validateMandatoryFields(dto)
            .throwIfFail()
    }

    private fun validateMandatoryFields(dto: ConflictFieldsDto): HashMap<String, ArrayList<String>> {
        return HashMap<String, ArrayList<String>>()
            .addErrorIf("title", max(255)) {
                dto.title?.takeIf { it.isPresent }.let { it!!.get().length > 255 }
            }
            .addErrorIf("titleRu", max(255)) {
                dto.titleRu?.takeIf { it.isPresent }.let { it!!.get().length > 255 }
            }
            .addErrorIf("titleEn", max(255)) {
                dto.titleEn?.takeIf { it.isPresent }.let { it!!.get().length > 255 }
            }
            .addErrorIf("titleEs", max(255)) {
                dto.titleEs?.takeIf { it.isPresent }.let { it!!.get().length > 255 }
            }
            .addErrorIf("titleDe", max(255)) {
                dto.titleDe?.takeIf { it.isPresent }.let { it!!.get().length > 255 }
            }
            .addErrorIf("companyName", min(3)) {
                dto.companyName?.takeIf { it.isPresent }.let { it!!.get().length < 3 }
            }
            .addErrorIf("companyName", max(500)) {
                dto.companyName?.takeIf { it.isPresent }.let { it!!.get().length > 500 }
            }
    }
}
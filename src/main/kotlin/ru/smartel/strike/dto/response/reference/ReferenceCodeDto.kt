package ru.smartel.strike.dto.response.reference

import ru.smartel.strike.entity.reference.ReferenceWithCode

data class ReferenceCodeDto(
    val id: Long,
    val code: String
) {
    constructor(entity: ReferenceWithCode) : this(entity.id, entity.code)
}

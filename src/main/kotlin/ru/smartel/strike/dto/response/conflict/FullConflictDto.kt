package ru.smartel.strike.dto.response.conflict

import ru.smartel.strike.entity.ConflictEntity
import java.time.ZoneOffset

data class FullConflictDto(
    val latitude: Float,
    val longitude: Float,
    val companyName: String?,
    val dateFrom: Long?,
    val dateTo: Long?,
    val conflictReasonId: Long?,
    val conflictResultId: Long?,
    val industryId: Long?,
    val parentEventId: Long?,
    val createdAt: Long?
) {
    constructor(conflict: ConflictEntity) : this(
        latitude = conflict.latitude,
        longitude = conflict.longitude,
        companyName = conflict.companyName,
        dateFrom = conflict.dateFrom?.toEpochSecond(ZoneOffset.UTC),
        dateTo = conflict.dateTo?.toEpochSecond(ZoneOffset.UTC),
        conflictReasonId = conflict.reason?.id,
        conflictResultId = conflict.result!!.id,
        industryId = conflict.industry?.id,
        parentEventId = conflict.parentEvent?.id,
        createdAt = conflict.createdAt?.toEpochSecond(ZoneOffset.UTC)
    )
}
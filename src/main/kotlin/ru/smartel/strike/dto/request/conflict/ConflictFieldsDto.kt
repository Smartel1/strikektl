package ru.smartel.strike.dto.request.conflict

import java.util.*

data class ConflictFieldsDto(
    var title: Optional<String>?,
    var titleRu: Optional<String>?,
    var titleEn: Optional<String>?,
    var titleEs: Optional<String>?,
    var titleDe: Optional<String>?,
    var latitude: Float?,
    var longitude: Float?,
    var companyName: Optional<String>?,
    var dateFrom: Optional<Long>?,
    var dateTo: Optional<Long>?,
    var conflictReasonId: Optional<Long>?,
    var conflictResultId: Optional<Long>?,
    var industryId: Optional<Long>?,
    var parentEventId: Optional<Long>?,
    var mainTypeId: Optional<Long>?,
    var automanagingMainType: Boolean?,
)

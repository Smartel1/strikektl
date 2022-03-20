package ru.smartel.strike.dto.response.conflict

data class CountByReasonsByIndustry(
    val industryId: Long,
    val countByReason: List<CountByReason>,
)

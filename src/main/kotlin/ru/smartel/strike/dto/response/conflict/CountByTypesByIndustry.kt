package ru.smartel.strike.dto.response.conflict

data class CountByTypesByIndustry(
    val industryId: Long,
    val countByType: List<CountByType>,
)

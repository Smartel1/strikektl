package ru.smartel.strike.dto.response.conflict

data class CountByResultsByIndustry(
    val industryId: Long,
    val countByResult: List<CountByResult>,
)

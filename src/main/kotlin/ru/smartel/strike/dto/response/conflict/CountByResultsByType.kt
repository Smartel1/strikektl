package ru.smartel.strike.dto.response.conflict

data class CountByResultsByType(
    val typeId: Long,
    val countByResult: List<CountByResult>,
)

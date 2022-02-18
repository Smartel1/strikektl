package ru.smartel.strike.dto

data class ListWrapperDtoMeta(
    val total: Long,
    val currentPage: Int,
    val perPage: Int,
    val lastPage: Long
)
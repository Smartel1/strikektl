package ru.smartel.strike.dto

import kotlin.math.ceil

data class ListWrapperDtoMeta(
    val total: Long,
    val currentPage: Int,
    val perPage: Int,
    val lastPage: Long = ceil(total.toDouble() / perPage.toDouble()).toLong()
)
package ru.smartel.strike.dto.exception

data class ApiErrorDto(
    val message: String,
    val errors: List<String>
) {
    constructor(message: String, error: String?) : this(message, error?.let { listOf(it) } ?: listOf())
}
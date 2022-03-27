package ru.smartel.strike.dto.request.conflict

import io.swagger.annotations.ApiParam

data class ConflictListRequestDto(
    @field:ApiParam(value = "Вывести краткую информацию о конфликтах")
    var brief: Boolean,
    var filters: ConflictFiltersDto? = ConflictFiltersDto()
)

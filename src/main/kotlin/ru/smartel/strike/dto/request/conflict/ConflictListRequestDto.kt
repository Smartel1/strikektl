package ru.smartel.strike.dto.request.conflict

import io.swagger.annotations.ApiParam
import ru.smartel.strike.service.Locale

data class ConflictListRequestDto(
    var locale: Locale?,
    @ApiParam(value = "Вывести краткую информацию о конфликтах")
    var brief: Boolean,
    var filters: ConflictFiltersDto? = ConflictFiltersDto()
)

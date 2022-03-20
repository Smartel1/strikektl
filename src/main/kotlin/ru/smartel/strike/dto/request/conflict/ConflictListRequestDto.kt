package ru.smartel.strike.dto.request.conflict

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.annotations.ApiParam
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.Locale

data class ConflictListRequestDto(
    val locale: Locale?,
    var user: UserPrincipal?,
    @ApiParam(value = "Вывести краткую информацию о конфликтах")
    val brief: Boolean,
    val filters: ConflictFiltersDto?,
    @JsonUnwrapped
    val baseListFields: BaseListRequestDto todo исправить тут протестировать
)

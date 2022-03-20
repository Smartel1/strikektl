package ru.smartel.strike.dto.request

import io.swagger.annotations.ApiParam

private const val DEFAULT_PAGE_CAPACITY = 20
private const val DEFAULT_PAGE = 1

data class BaseListRequestDto(
    @ApiParam(value = "Количество элементов на странице ответа (неменьше 1)")
    val perPage: Int = DEFAULT_PAGE_CAPACITY,
    @ApiParam(value = "Номер страницы ответа (неменьше 1)")
    val page: Int = DEFAULT_PAGE,
    val sort: Sort?,
)

data class Sort(
    @ApiParam(value = "Поле для сортировки (для конфликтов доступно: createdAt, для новостей и событий: createdAt, date, views)")
    var field: String?,
    @ApiParam(value = "Порядок сортировки", allowableValues = "desc, asc")
    val order: String?,
)
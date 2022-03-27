package ru.smartel.strike.dto.request

import io.swagger.annotations.ApiParam

private const val DEFAULT_PAGE_CAPACITY = 20
private const val DEFAULT_PAGE = 1

open class BaseListRequestDto(
    @field:ApiParam(value = "Количество элементов на странице ответа (неменьше 1)")
    var perPage: Int = DEFAULT_PAGE_CAPACITY,
    @field:ApiParam(value = "Номер страницы ответа (неменьше 1)")
    var page: Int = DEFAULT_PAGE,
    var sort: Sort?
)

data class Sort(
    @field:ApiParam(value = "Поле для сортировки (для конфликтов доступно: createdAt, для новостей и событий: createdAt, date, views)")
    var field: String? = null,
    @field:ApiParam(value = "Порядок сортировки", allowableValues = "desc, asc")
    var order: String? = null
)
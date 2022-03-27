package ru.smartel.strike.dto.request.conflict

import io.swagger.annotations.ApiParam

data class ConflictFiltersDto(
    @field:ApiParam(value = "Фильтр отсекает те конфликты, которые закончились строго ранее этой даты")
    var dateFrom: Int? = null,
    @field:ApiParam(value = "Фильтр отсекает те конфликты, которые начались строго позже этой даты")
    var dateTo: Int? = null,
    @field:ApiParam(value = "Полнотекстовый поиск. В выбору попадут конфликты, в названии которых содержится заданный текст, а также если текст содержится в заголовках или описаниях событий (на всех языках)")
    var fulltext: String? = null,
    @field:ApiParam(value = "Id результата конфликта (из справочника)")
    var conflictResultIds: List<Long>? = null,
    @field:ApiParam(value = "Id причины конфликта (из справочника)")
    var conflictReasonIds: List<Long>? = null,
    @field:ApiParam(value = "Id отрасли конфликта (из справочника)")
    var industryIds: List<Long>? = null,
    @field:ApiParam(value = "Id основной формы конфликта (из справочника типов событий). Допускается null")
    var mainTypeIds: List<String>? = null,
    @field:ApiParam(value = "Id конфликта. Фильтр выводит только те конфликты, которые являются родителями (не только прямыми) переданного конфликта")
    var ancestorsOf: Long? = null,
    @field:ApiParam(value = "Id конфликта. Фильтр выводит только те конфликты, которые являются прямыми потомками переданного конфликта")
    var childrenOf: Long? = null,
    var near: SearchArea? = null,
    @field:ApiParam(value = "true - выведутся только избранные, иначе - все. Работает только для аутентифицированных пользователей")
    var favourites: Boolean? = null,
)

data class SearchArea(
    @field:ApiParam(value = "Широта центра поиска")
    var lat: Float?,
    @field:ApiParam(value = "Долгота центра поиска")
    var lng: Float?,
    @field:ApiParam(value = "Радиус поиска в километрах")
    var radius: Int?,
)

package ru.smartel.strike.dto.request.conflict

import io.swagger.annotations.ApiParam

data class ConflictFiltersDto(
    @ApiParam(value = "Фильтр отсекает те конфликты, которые закончились строго ранее этой даты")
    var dateFrom: Int? = null,
    @ApiParam(value = "Фильтр отсекает те конфликты, которые начались строго позже этой даты")
    val dateTo: Int? = null,
    @ApiParam(value = "Полнотекстовый поиск. В выбору попадут конфликты, в названии которых содержится заданный текст, а также если текст содержится в заголовках или описаниях событий (на всех языках)")
    val fulltext: String? = null,
    @ApiParam(value = "Id результата конфликта (из справочника)")
    val conflictResultIds: List<Long>? = null,
    @ApiParam(value = "Id причины конфликта (из справочника)")
    val conflictReasonIds: List<Long>? = null,
    @ApiParam(value = "Id отрасли конфликта (из справочника)")
    val industryIds: List<Long>? = null,
    @ApiParam(value = "Id основной формы конфликта (из справочника типов событий). Допускается null")
    val mainTypeIds: List<String>? = null,
    @ApiParam(value = "Id конфликта. Фильтр выводит только те конфликты, которые являются родителями (не только прямыми) переданного конфликта")
    val ancestorsOf: Long? = null,
    @ApiParam(value = "Id конфликта. Фильтр выводит только те конфликты, которые являются прямыми потомками переданного конфликта")
    val childrenOf: Long? = null,
    val near: SearchArea? = null,
    @ApiParam(value = "true - выведутся только избранные, иначе - все. Работает только для аутентифицированных пользователей")
    val favourites: Boolean? = null,
)

data class SearchArea(
    @ApiParam(value = "Широта центра поиска")
    var lat: Float?,
    @ApiParam(value = "Долгота центра поиска")
    val lng: Float?,
    @ApiParam(value = "Радиус поиска в километрах")
    val radius: Int?,
)

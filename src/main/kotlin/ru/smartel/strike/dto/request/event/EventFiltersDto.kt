package ru.smartel.strike.dto.request.event

import io.swagger.annotations.ApiParam
import ru.smartel.strike.dto.request.post.PostFiltersDto

data class EventFiltersDto(
    @field:ApiParam(value = "Идентификаторы конфликтов")
    var conflictIds: List<Long>? = null,
    @field:ApiParam(value = "Идентификаторы статусов событий")
    var eventStatusIds: List<Long>? = null,
    @field:ApiParam(value = "Идентификаторы типов событий")
    var eventTypeIds: List<Long>? = null,
    @field:ApiParam(value = "Идентификаторы стран")
    var countryIds: List<Long>? = null,
    @field:ApiParam(value = "Идентификаторы регионов")
    var regionIds: List<Long>? = null,
    @field:ApiParam(value = "Идентификаторы отраслей конфликтов")
    var industryIds: List<Long>? = null,
    var near: SearchArea? = null,
    @field:ApiParam(value = "Массив тегов. В выборку попадут посты, содержащие хотя бы один из указанных тегов")
    override var tags: List<String>? = null,
    @field:ApiParam(value = "Фильтр отсекает посты, которые произошли до этой даты")
    override var dateFrom: Int? = null,
    @field:ApiParam(value = "Фильтр отсекает посты, которые произошли после этой даты")
    override var dateTo: Int? = null,
    @field:ApiParam(value = "true - выведутся только избранные, иначе - все. Работает только для аутентифицированных пользователей")
    override var favourites: Boolean? = null,
    @field:ApiParam(value = "Если true, то только опубликованные, false - только неопубликованные, по-умолчанию - все")
    override var published: Boolean? = null,
    @field:ApiParam(value = "Фильтр для полнотекстового поиска. Ищет подстроку в заголовках и содержимом на всех языках")
    override var fulltext: String? = null
) : PostFiltersDto

data class SearchArea(
    @field:ApiParam(value = "Широта центра поиска")
    var lat: Float?,
    @field:ApiParam(value = "Долгота центра поиска")
    var lng: Float?,
    @field:ApiParam(value = "Радиус поиска в километрах")
    var radius: Int?,
)

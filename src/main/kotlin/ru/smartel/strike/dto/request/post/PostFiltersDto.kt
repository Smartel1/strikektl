package ru.smartel.strike.dto.request.post

interface PostFiltersDto {
    var tags: List<String>?
    var dateFrom: Int?
    var dateTo: Int?
    var favourites: Boolean?
    var published: Boolean?
    var fulltext: String?
}

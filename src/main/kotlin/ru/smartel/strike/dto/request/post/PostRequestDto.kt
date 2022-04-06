package ru.smartel.strike.dto.request.post

import ru.smartel.strike.dto.request.video.VideoDto
import java.util.*

interface PostRequestDto {
    var published: Optional<Boolean>?
    var title: Optional<String?>?
    var titleRu: Optional<String?>?
    var titleEn: Optional<String?>?
    var titleEs: Optional<String?>?
    var titleDe: Optional<String?>?
    var content: Optional<String?>?
    var contentRu: Optional<String?>?
    var contentEn: Optional<String?>?
    var contentEs: Optional<String?>?
    var contentDe: Optional<String?>?
    var date: Optional<Long?>?
    var sourceLink: Optional<String?>?
    var tags: Optional<List<String>>?
    var photoUrls: Optional<List<String>>?
    var videos: Optional<List<VideoDto>>?
    var publishTo: Set<Long>?
    var pushRequired: Boolean
}
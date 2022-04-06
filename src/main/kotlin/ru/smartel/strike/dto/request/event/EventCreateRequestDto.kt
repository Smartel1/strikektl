package ru.smartel.strike.dto.request.event

import ru.smartel.strike.dto.request.post.PostRequestDto
import ru.smartel.strike.dto.request.video.VideoDto
import java.util.*

data class EventCreateRequestDto(
    var conflictId: Optional<Long>?,
    var latitude: Optional<Float>?,
    var longitude: Optional<Float>?,
    var localityId: Optional<Long>?,
    var eventTypeId: Optional<Long>?,

    override var published: Optional<Boolean>?,
    override var title: Optional<String?>?,
    override var titleRu: Optional<String?>?,
    override var titleEn: Optional<String?>?,
    override var titleEs: Optional<String?>?,
    override var titleDe: Optional<String?>?,
    override var content: Optional<String?>?,
    override var contentRu: Optional<String?>?,
    override var contentEn: Optional<String?>?,
    override var contentEs: Optional<String?>?,
    override var contentDe: Optional<String?>?,
    override var date: Optional<Long?>?,
    override var sourceLink: Optional<String?>?,
    override var tags: Optional<List<String>>?,
    override var photoUrls: Optional<List<String>>?,
    override var videos: Optional<List<VideoDto>>?,
    override var publishTo: Set<Long>? = emptySet(),
    override var pushRequired: Boolean = true
) : PostRequestDto

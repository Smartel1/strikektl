package ru.smartel.strike.dto.response.video

import ru.smartel.strike.entity.VideoEntity

data class VideoDto(
    val url: String,
    val previewUrl: String?,
    val videoTypeId: Long
) {
    constructor(entity: VideoEntity) : this(entity.url, entity.previewUrl, entity.videoType.id)
}

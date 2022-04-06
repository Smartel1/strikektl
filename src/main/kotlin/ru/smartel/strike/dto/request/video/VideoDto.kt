package ru.smartel.strike.dto.request.video

import java.util.*

data class VideoDto(
    var url: String?,
    var previewUrl: Optional<String>?,
    var videoTypeId: Long?
)

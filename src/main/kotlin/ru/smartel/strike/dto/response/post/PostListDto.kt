package ru.smartel.strike.dto.response.post

import com.fasterxml.jackson.annotation.JsonUnwrapped
import ru.smartel.strike.dto.response.ContentsDto
import ru.smartel.strike.dto.response.TitlesDto
import ru.smartel.strike.dto.response.video.VideoDto
import ru.smartel.strike.entity.interfaces.Post
import ru.smartel.strike.service.Locale
import java.time.ZoneOffset

data class PostListDto(
    val id: Long,
    val published: Boolean,
    val date: Long,
    val views: Int,
    val sourceLink: String?,
    val photoUrls: List<String>,
    val videos: List<VideoDto>,
    val tags: List<String>,
    @JsonUnwrapped
    val titles: TitlesDto,
    @JsonUnwrapped
    val contents: ContentsDto
) {
    constructor(entity: Post, locale: Locale) : this(
        id = entity.id,
        published = entity.post.published,
        date = entity.post.date.toEpochSecond(ZoneOffset.UTC),
        views = entity.post.views,
        sourceLink = entity.post.sourceLink,
        photoUrls = entity.photos.map { it.url },
        videos = entity.videos.map { VideoDto(it) },
        tags = entity.tags.map { it.name },
        titles = TitlesDto(entity, locale),
        contents = ContentsDto(entity, locale)
    )
}
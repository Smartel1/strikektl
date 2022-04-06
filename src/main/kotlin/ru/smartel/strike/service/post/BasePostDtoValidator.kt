package ru.smartel.strike.service.post

import ru.smartel.strike.dto.request.post.PostRequestDto
import ru.smartel.strike.dto.service.network.Network
import ru.smartel.strike.util.*
import java.util.*
import kotlin.collections.HashMap

open class BasePostDtoValidator {
    fun validateStoreDto(dto: PostRequestDto): HashMap<String, ArrayList<String>> {
        val availableNetworkIds = EnumSet.allOf(Network::class.java).map { it.id }
        return HashMap<String, ArrayList<String>>()
            .addErrorIf("date", required()) { dto.date == null }
            .addErrorIf("date", notNull()) { dto.date?.isEmpty == true }
            .addErrorIf("publishTo", notNull()) { dto.publishTo == null }
            .addErrorIf("publishTo", oneOf(availableNetworkIds)) {
                dto.publishTo != null && dto.publishTo!!.any { !availableNetworkIds.contains(it) }
            }
            .validateCommon(dto)
    }

    private fun HashMap<String, ArrayList<String>>.validateCommon(dto: PostRequestDto): HashMap<String, ArrayList<String>> {
        val errors = addErrorIf("published", notNull()) { dto.publishTo?.isEmpty() == true }
            .addErrorIf("title", max(255)) {
                dto.title?.takeIf { it.isPresent }?.let { it.get().length > 255 } == true
            }
            .addErrorIf("titleRu", max(255)) {
                dto.titleRu?.takeIf { it.isPresent }?.let { it.get().length > 255 } == true
            }
            .addErrorIf("titleEn", max(255)) {
                dto.titleEn?.takeIf { it.isPresent }?.let { it.get().length > 255 } == true
            }
            .addErrorIf("titleEs", max(255)) {
                dto.titleEs?.takeIf { it.isPresent }?.let { it.get().length > 255 } == true
            }
            .addErrorIf("titleDe", max(255)) {
                dto.titleDe?.takeIf { it.isPresent }?.let { it.get().length > 255 } == true
            }
            .addErrorIf("sourceLink", max(255)) {
                dto.sourceLink?.takeIf { it.isPresent }?.let { it.get().length > 255 } == true
            }
            .addErrorIf("content", min(3)) {
                dto.content?.takeIf { it.isPresent }?.let { it.get().length < 3 } == true
            }
            .addErrorIf("contentRu", min(3)) {
                dto.contentRu?.takeIf { it.isPresent }?.let { it.get().length < 3 } == true
            }
            .addErrorIf("contentEn", min(3)) {
                dto.contentEn?.takeIf { it.isPresent }?.let { it.get().length < 3 } == true
            }
            .addErrorIf("contentEs", min(3)) {
                dto.contentEs?.takeIf { it.isPresent }?.let { it.get().length < 3 } == true
            }
            .addErrorIf("contentDe", min(3)) {
                dto.contentDe?.takeIf { it.isPresent }?.let { it.get().length < 3 } == true
            }
            .addErrorIf("photoUrls", notNull()) { dto.photoUrls?.isEmpty == true }
            .addErrorIf("tags", notNull()) { dto.tags?.isEmpty == true }
            .addErrorIf("videos", notNull()) { dto.videos?.isEmpty == true }

        dto.photoUrls?.takeIf { it.isPresent }?.let {
            it.get().forEachIndexed { index, photoUrl ->
                errors.addErrorIf("photoUrls[$index]", max(500)) { photoUrl.length > 500 }
            }
        }

        dto.tags?.takeIf { it.isPresent }?.let {
            it.get().forEachIndexed { index, tag ->
                errors.addErrorIf("tags[$index]", max(20)) { tag.length > 20 }
                    .addErrorIf("tags[$index]", min(2)) { tag.length < 2 }
            }
        }

        dto.videos?.takeIf { it.isPresent }?.let {
            it.get().forEachIndexed { index, video ->
                errors.addErrorIf("videos[$index].url", notNull()) { video.url == null }
                    .addErrorIf("videos[$index].url", max(500)) {
                        video.url?.let { url -> url.length > 500 } ?: false
                    }
                    .addErrorIf("videos[$index].previewUrl", max(500)) {
                        video.previewUrl?.takeIf { p -> p.isPresent }?.let { p -> p.get().length > 500 } ?: false
                    }
                    .addErrorIf("videos[$index].videoTypeId", notNull()) { video.videoTypeId == null }
            }
        }

        return errors
    }
}
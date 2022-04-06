package ru.smartel.strike.service.publish

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.smartel.strike.entity.EventEntity
import ru.smartel.strike.entity.NewsEntity
import ru.smartel.strike.entity.interfaces.Post
import ru.smartel.strike.service.integration.PublicationGateway

@Service
class PostPublicationService(
    private val publicationGateway: PublicationGateway,
    @Value("\${site-url}")
    private val siteUrl: String
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(PostPublicationService::class.java)
    }

    fun publishAndSetFlags(post: Post, publishTo: Set<Long?>?) {
//        if (Objects.nonNull(post.titleRu)) {
//            logger.debug("Publishing post {} to networks {}", post, publishTo)
//            val sitePageUrl = getPostUrl(post)
//            publicationGateway.publish(
//                PublishDTOWithNetworks(
//                    PublishDTO(
//                        post.getContentRu(),
//                        post.getSourceLink(),
//                        sitePageUrl,
//                        post.getVideos().stream().map(Video::getUrl).collect(Collectors.toList())
//                    ),
//                    getNetworkIdsToSendTo(post, publishTo)
//                )
//            )
//            setSentToFlags(post, publishTo)
//        }
    }

    fun getPostUrl(postEntity: Post): String {
        if (postEntity.javaClass === EventEntity::class.java) {
            return siteUrl + "events/" + postEntity.id
        }
        if (postEntity.javaClass === NewsEntity::class.java) {
            return siteUrl + "news/" + postEntity.id
        }
        throw IllegalArgumentException("Unknown post type")
    }
}
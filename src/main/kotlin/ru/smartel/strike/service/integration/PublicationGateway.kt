package ru.smartel.strike.service.integration

import org.springframework.integration.annotation.MessagingGateway
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * Gateway for publishing posts to networks such as VK, telegram, OK etc
 */
@Component
@MessagingGateway(defaultRequestChannel = "publicationFlow.input")
interface PublicationGateway {
    /**
     * Publish post to networks
     *
     * @param message publishDto and set of network ids
     */
//    @Async
//    fun publish(message: PublishDTOWithNetworks?)
}
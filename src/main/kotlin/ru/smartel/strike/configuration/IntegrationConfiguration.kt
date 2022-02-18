package ru.smartel.strike.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.RouterSpec
import org.springframework.integration.router.ExpressionEvaluatingRouter
import org.springframework.integration.splitter.AbstractMessageSplitter
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import ru.smartel.strike.dto.publication.PublishDto
import ru.smartel.strike.dto.publication.PublishDtoWithNetworks
import ru.smartel.strike.service.publish.OkService
import ru.smartel.strike.service.publish.TelegramService
import ru.smartel.strike.service.publish.VkService
import ru.smartel.strike.service.sort.network.Network

@Configuration
class IntegrationConfiguration {
    val networkHeader = "networkId"

    @Bean
    fun publicationFlow(): IntegrationFlow {
        return IntegrationFlow { f ->
            f
                .filter<PublishDtoWithNetworks> { data -> data.publishTo.isEmpty() }
                .split(splitter())
                .transform<PublishDto, PublishDto> { data ->
                    //if videos contain sourceUrl then remove this url from videos
                    data.videoUrls.remove(data.sourceUrl)
                    data
                }
                .route("headers.$networkHeader") { r: RouterSpec<Any, ExpressionEvaluatingRouter> ->
                    r
                        .channelMapping(Network.TELEGRAM.id, TelegramService.telegramChannel)
                        .channelMapping(Network.OK.id, OkService.okChannel)
                        .channelMapping(Network.VK.id, VkService.vkChannel)
                }
        }
    }

    /**
     * Splits messages by destination Network and sets appropriate header
     */
    fun splitter() = object : AbstractMessageSplitter() {
        override fun splitMessage(message: Message<*>?): Any {
            val dto = message!!.payload as PublishDtoWithNetworks
            return dto.publishTo.map { networkId ->
                MessageBuilder.withPayload(dto.data)
                    .setHeaderIfAbsent(networkHeader, networkId)
                    .build()
            }
        }
    }
}
package ru.smartel.strike.service.notifications

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.smartel.strike.configuration.properties.PushProperties
import ru.smartel.strike.service.Locale
import java.util.concurrent.CompletableFuture

@Service
class PushService(
    private val firebaseMessaging: FirebaseMessaging?,
    private val pushProperties: PushProperties
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(PushService::class.java)
    }

    fun eventCreatedByUser(eventId: Long, authorId: Long, authorName: String?) {
        sendAsyncIfEnabled {
            logger.info("sending notification about event {} proposed by user {}", eventId, authorName)
            Message.builder()
                .setTopic(pushProperties.topics.admin)
                .setNotification(
                    Notification(
                        "ЗабастКом",
                        "На модерации событие от $authorName"
                    )
                )
                .putData("id", eventId.toString())
                .putData("creatorName", authorName)
                .putData("creatorId", authorId.toString())
                .putData("type", "admin") // wth is that?
                .build()
        }
    }

    fun eventPublished(
        eventId: Long,
        authorId: Long,
        lng: Float,
        lat: Float,
        eventTypeId: Long?,
        regionId: Long?,
        titlesByLocales: Map<Locale, String>,
        authorFCM: String?,
        notifyAuthor: Boolean
    ) {
        for ((key, value) in titlesByLocales) {
            eventPublishedByLocale(eventId, authorId, lng, lat, eventTypeId, regionId, value, key)
        }
        if (notifyAuthor) {
            sendYourEventModerated(eventId, authorId, lng, lat, authorFCM)
        }
    }

    /**
     * Notify users of topic about event publication
     * For each locale we have independent topic
     *
     * @param eventId  event id
     * @param authorId author id
     * @param title    event title by locale
     * @param locale   locale
     */
    private fun eventPublishedByLocale(
        eventId: Long,
        authorId: Long,
        lng: Float,
        lat: Float,
        eventTypeId: Long?,
        regionId: Long?,
        title: String?,
        locale: Locale
    ) {
        sendAsyncIfEnabled {
            logger.info("sending notification about event publishing to {} topic. Title: {}", locale, title)
            val topic: String
            val notificationTitle: String
            val notificationBody: String
            when (locale) {
                Locale.RU -> {
                    topic = pushProperties.topics.events.ru
                    notificationTitle = "ЗабастКом"
                    notificationBody = title ?: "В приложении опубликовано событие"
                }
                Locale.EN -> {
                    topic = pushProperties.topics.events.en
                    notificationTitle = "ZabastCom"
                    notificationBody = title ?: "Event was published"
                }
                Locale.ES -> {
                    topic = pushProperties.topics.events.es
                    notificationTitle = "ZabastCom"
                    notificationBody = title ?: "El evento ha sido publicado"
                }
                Locale.DE -> {
                    topic = pushProperties.topics.events.de
                    notificationTitle = "ZabastCom"
                    notificationBody =
                        title ?: "Eine Veranstaltung wird in der Anwendung veröffentlicht"
                }
                else -> throw IllegalStateException("unknown topic for locale $locale")
            }
            Message.builder()
                .setTopic(topic)
                .setNotification(Notification(notificationTitle, notificationBody))
                .putData("id", eventId.toString())
                .putData("lat", lat.toString())
                .putData("lng", lng.toString())
                .putData("creatorId", authorId.toString())
                .putData("title", title)
                .putData("eventTypeId", eventTypeId.toString())
                .putData("regionId", regionId.toString())
                .putData("type", "news") // wth is that?
                .build()
        }
    }

    /**
     * Notify event's author about publication
     *
     * @param eventId   event id
     * @param authorId  author id
     * @param lng       event lng
     * @param lat       event lat
     * @param authorFCM author FCM registration token
     */
    private fun sendYourEventModerated(eventId: Long, authorId: Long, lng: Float, lat: Float, authorFCM: String?) {
        if (authorFCM == null) {
            return
        }
        sendAsyncIfEnabled {
            logger.info(
                "sending notification about event {} publication to user {} with FCM token {}",
                eventId,
                authorId,
                authorFCM
            )
            Message.builder()
                .setToken(authorFCM) //idk why always Russian
                .setNotification(
                    Notification(
                        "Забастком",
                        "Предложенный Вами пост прошел модерацию"
                    )
                )
                .putData("id", eventId.toString())
                .putData("messageRu", "Предложенный Вами пост прошел модерацию")
                .putData("messageEn", "The news you proposed was published")
                .putData("messageEs", "Las noticias que propusiste fueron publicadas")
                .putData("messageDe", "Ihr Beitrag wurde moderiert")
                .putData("creatorId", authorId.toString())
                .putData("lat", lat.toString())
                .putData("lng", lng.toString())
                .putData("type", "moderated") // wth is that?
                .build()
        }
    }

    /**
     * Send push to Firebase if push notifications are enabled
     *
     * @param messageSupplier message supplier
     */
    private fun sendAsyncIfEnabled(messageSupplier: () -> Message) {
        if (firebaseMessaging == null || !pushProperties.enabled) {
            return
        }
        CompletableFuture.runAsync {
            try {
                firebaseMessaging.send(messageSupplier())
            } catch (e: FirebaseMessagingException) {
                logger.error("Push notification send error", e)
            }
        }
    }
}
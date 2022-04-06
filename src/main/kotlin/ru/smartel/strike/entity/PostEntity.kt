package ru.smartel.strike.entity

import ru.smartel.strike.service.Locale
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Embeddable
class PostEntity(
    @Column(name = "title_ru")
    var titleRu: String?,
    @Column(name = "title_en")
    var titleEn: String?,
    @Column(name = "title_es")
    var titleEs: String?,
    @Column(name = "title_de")
    var titleDe: String?,
    @Column(name = "content_ru")
    var contentRu: String?,
    @Column(name = "content_en")
    var contentEn: String?,
    @Column(name = "content_es")
    var contentEs: String?,
    @Column(name = "content_de")
    var contentDe: String?,
    @Column(name = "date")
    var date: LocalDateTime,
    @Column(name = "views")
    var views: Int = 0,
    @Column(name = "published")
    var published: Boolean,
    @Column(name = "source_link")
    var sourceLink: String?,
    @Column(name = "sent_to_ok")
    var sentToOk: Boolean = false,
    @Column(name = "sent_to_vk")
    var sentToVk: Boolean = false,
    @Column(name = "sent_to_telegram")
    var sentToTelegram: Boolean = false,
    @Column(name = "sent_push_ru")
    var sentPushRu: Boolean = false,
    @Column(name = "sent_push_en")
    var sentPushEn: Boolean = false,
    @Column(name = "sent_push_es")
    var sentPushEs: Boolean = false,
    @Column(name = "sent_push_de")
    var sentPushDe: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    var author: UserEntity,
) {
    fun setPushFlagsForLocales(locales: Set<Locale>) {
        if (locales.contains(Locale.RU)) { sentPushRu = true }
        if (locales.contains(Locale.EN)) { sentPushEn = true }
        if (locales.contains(Locale.ES)) { sentPushEs = true }
        if (locales.contains(Locale.DE)) { sentPushDe = true }
    }
}

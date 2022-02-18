package ru.smartel.strike.entity

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
    var views: Int,
    @Column(name = "published")
    var published: Boolean,
    @Column(name = "source_link")
    var sourceLink: String?,
    @Column(name = "sent_to_ok")
    var sentToOk: Boolean,
    @Column(name = "sent_to_vk")
    var sentToVk: Boolean,
    @Column(name = "sent_to_telegram")
    var sentToTelegram: Boolean,
    @Column(name = "sent_push_ru")
    var sentPushRu: Boolean,
    @Column(name = "sent_push_en")
    var sentPushEn: Boolean,
    @Column(name = "sent_push_es")
    var sentPushEs: Boolean,
    @Column(name = "sent_push_de")
    var sentPushDe: Boolean,
    @ManyToOne(fetch = FetchType.LAZY)
    var author: UserEntity,
)

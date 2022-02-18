package ru.smartel.strike.entity

import org.hibernate.annotations.CreationTimestamp
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.Locale.*
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "client_versions")
data class ClientVersionEntity (
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     var id: Long = 0,

     @field:CreationTimestamp
     @Column(name = "created_at")
     var createdAt: LocalDateTime? = null,

     @field:CreationTimestamp
     @Column(name = "updated_at")
     var updatedAt: LocalDateTime? = null,

     var version: String,

     @Column(name = "client_id")
     var clientId: String,

     var required: Boolean,

     @Column(name = "description_ru")
     var descriptionRu: String,

     @Column(name = "description_en")
     var descriptionEn: String,

     @Column(name = "description_es")
     var descriptionEs: String,

     @Column(name = "description_de")
     var descriptionDe: String
) {
    fun getDescriptionByLocale(locale: Locale?): String {
        return when (locale) {
            RU -> descriptionRu
            EN -> descriptionEn
            ES -> descriptionEs
            DE -> descriptionDe
            else -> ""
        }
    }
}
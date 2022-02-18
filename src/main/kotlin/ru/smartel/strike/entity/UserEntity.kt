package ru.smartel.strike.entity

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.TextNode
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.springframework.data.annotation.AccessType
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
@TypeDef(name = "jsonb", typeClass = JsonNodeBinaryType::class)
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @AccessType(AccessType.Type.PROPERTY) // in order to get id without loading whole entity from db
    var id: Long = 0,

    @field:CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @field:CreationTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "uid")
    var uid: String,

    @Column(name = "name")
    var name: String?,

    /**
     * FCM registration token (firebase cloud messaging)
     */
    @Column(name = "fcm")
    var fcm: String?,

    @Column(name = "image_url")
    var imageUrl: String?,

    @Column(name = "email")
    var email: String?,

    @Column(name = "roles")
    @Type(type = "jsonb")
    var roles: JsonNode = ArrayNode(JsonNodeFactory.instance),

    @ManyToMany
    @JoinTable(
        name = "favourite_events",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "event_id")]
    )
    var favouriteEvents: List<EventEntity>,

    @ManyToMany
    @JoinTable(
        name = "favourite_news",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "news_id")]
    )
    var favouriteNews: List<NewsEntity>,

    @ManyToMany
    @JoinTable(
        name = "favourite_conflicts",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "conflict_id")]
    )
    var favouriteConflicts: List<ConflictEntity>
) {
    fun getRolesAsList(): List<String> = roles.map { it.asText() }
    fun setRoles(roles: List<String>) {
        this.roles = ArrayNode(JsonNodeFactory.instance, roles.map { TextNode.valueOf(it) })
    }
}
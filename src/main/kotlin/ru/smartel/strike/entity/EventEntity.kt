package ru.smartel.strike.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import ru.smartel.strike.entity.interfaces.Post
import ru.smartel.strike.entity.reference.EventStatusEntity
import ru.smartel.strike.entity.reference.EventTypeEntity
import ru.smartel.strike.entity.reference.LocalityEntity
import java.time.LocalDateTime
import javax.persistence.*
import org.springframework.data.annotation.AccessType as AccessType1

@Entity
@Table(name = "events")
class EventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @AccessType1(AccessType1.Type.PROPERTY) // in order to get id without loading whole entity from db
    override var id: Long = 0,

    @Embedded
    override var post: PostEntity,

    @field:CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @field:CreationTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "latitude")
    var latitude: Float,

    @Column(name = "longitude")
    var longitude: Float,

    @ManyToMany(mappedBy = "favouriteEvents")
    var likedUsers: Set<UserEntity>,

    @ManyToMany(cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    @JoinTable(
        name = "event_photo",
        joinColumns = [JoinColumn(name = "event_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "photo_id", referencedColumnName = "id")]
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var photos: Set<PhotoEntity> = HashSet(),

    @ManyToMany(cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    @JoinTable(
        name = "event_video",
        joinColumns = [JoinColumn(name = "event_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "video_id", referencedColumnName = "id")]
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var videos: Set<VideoEntity> = HashSet(),

    @ManyToMany(cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    @JoinTable(
        name = "event_tag",
        joinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "video_id", referencedColumnName = "id")]
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var tags: Set<TagEntity> = HashSet(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conflict_id")
    var conflict: ConflictEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_status_id")
    @AccessType1(AccessType1.Type.PROPERTY) // todo проверить необходимость этой аннотации
    var status: EventStatusEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    var type: EventTypeEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id")
    var locality: LocalityEntity
) : Post
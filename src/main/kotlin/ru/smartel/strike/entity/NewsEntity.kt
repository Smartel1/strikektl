package ru.smartel.strike.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import ru.smartel.strike.entity.interfaces.Post
import java.time.LocalDateTime
import javax.persistence.*
import org.springframework.data.annotation.AccessType as AccessType1

@Entity
@Table(name = "news")
class NewsEntity(
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

    @ManyToMany(mappedBy = "favouriteEvents")
    var likedUsers: Set<UserEntity>,

    @ManyToMany(cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    @JoinTable(
        name = "news_photo",
        joinColumns = [JoinColumn(name = "news_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "photo_id", referencedColumnName = "id")]
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var photos: Set<PhotoEntity> = HashSet(),

    @ManyToMany(cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    @JoinTable(
        name = "news_video",
        joinColumns = [JoinColumn(name = "news_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "video_id", referencedColumnName = "id")]
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var videos: Set<VideoEntity> = HashSet(),

    @ManyToMany(cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    @JoinTable(
        name = "news_tag",
        joinColumns = [JoinColumn(name = "event_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "video_id", referencedColumnName = "id")]
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var tags: Set<TagEntity> = HashSet()
) : Post
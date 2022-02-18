package ru.smartel.strike.entity

import org.hibernate.annotations.CreationTimestamp
import ru.smartel.strike.entity.reference.VideoTypeEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "videos")
class VideoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @field:CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @field:CreationTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "url")
    var url: String,

    @Column(name = "preview_url")
    var previewUrl: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_type_id", referencedColumnName = "id")
    var videoType: VideoTypeEntity
)
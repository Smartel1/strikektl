package ru.smartel.strike.entity

import org.hibernate.annotations.CreationTimestamp
import pl.exsio.nestedj.model.NestedNode
import ru.smartel.strike.entity.`interface`.HavingTitles
import ru.smartel.strike.entity.reference.ConflictReasonEntity
import ru.smartel.strike.entity.reference.ConflictResultEntity
import ru.smartel.strike.entity.reference.EventTypeEntity
import ru.smartel.strike.entity.reference.IndustryEntity
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "conflicts")
data class ConflictEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @field:CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @field:CreationTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "parent_id")
    var parent: Long?,

    @Column(name = "lft", nullable = false)
    var left: Long?, //dont set default value. Let it fail if not set while saving entity

    @Column(name = "rgt", nullable = false)
    var right: Long, //dont set default value. Let it fail if not set while saving entity

    @Column(name = "lvl", nullable = false)
    var level: Long, //dont set default value. Let it fail if not set while saving entity

    @Column(name = "title_ru")
    override var titleRu: String?,

    @Column(name = "title_en")
    override var titleEn: String?,

    @Column(name = "title_es")
    override var titleEs: String?,

    @Column(name = "title_de")
    override var titleDe: String?,

    @Column(name = "latitude", nullable = false)
    var latitude: Float,

    @Column(name = "longitude", nullable = false)
    var longitude: Float,

    @Size(max = 500)
    @Column(name = "company_name", length = 500)
    var companyName: String?,

    @Column(name = "date_from")
    var dateFrom: LocalDateTime?,

    @Column(name = "date_to")
    var dateTo: LocalDateTime?,

    @OneToMany(cascade = [CascadeType.DETACH], mappedBy = "conflict", fetch = FetchType.LAZY)
    var events: List<EventEntity>,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_event_id")
    var parentEvent: EventEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conflict_reason_id")
    var reason: ConflictReasonEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conflict_result_id")
    var result: ConflictResultEntity?,

    /**
     * Calculated field. But can be set by admin
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_type_id")
    var mainType: EventTypeEntity?,

    /**
     * Whether automatically update mainType or not
     */
    @Column(name = "automanaging_main_type")
    var automanagingMainType: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    var industry: IndustryEntity?,

    @ManyToMany(mappedBy = "favouriteConflicts")
    var likedUsers: Set<UserEntity> = HashSet()
) : NestedNode<Long>, HavingTitles {
    override fun getId() = id
    override fun setId(value: Long?) {
        id = value!!
    }
    override fun getTreeLeft() = left
    override fun setTreeLeft(value: Long?) {
        left = value!!
    }
    override fun getTreeRight() = right
    override fun setTreeRight(value: Long?) {
        right = value!!
    }
    override fun getTreeLevel() = level
    override fun setTreeLevel(value: Long?) {
        level = value!!
    }
    override fun getParentId() = parent
    override fun setParentId(value: Long?) {
        parent = value!!
    }
}

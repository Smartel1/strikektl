package ru.smartel.strike.service.conflict

import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import ru.smartel.strike.dto.request.conflict.ConflictFiltersDto
import ru.smartel.strike.entity.ConflictEntity
import ru.smartel.strike.entity.EventEntity
import ru.smartel.strike.entity.PostEntity
import ru.smartel.strike.entity.UserEntity
import ru.smartel.strike.entity.reference.ConflictReasonEntity
import ru.smartel.strike.entity.reference.ConflictResultEntity
import ru.smartel.strike.entity.reference.IndustryEntity
import ru.smartel.strike.repository.conflict.ConflictRepository
import ru.smartel.strike.util.emptySpecification
import ru.smartel.strike.util.falseSpecification
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.persistence.criteria.JoinType

@Service
class ConflictFiltersTransformer(
    private val conflictRepository: ConflictRepository,
) {
    fun toSpecification(filters: ConflictFiltersDto?, userId: Long?): Specification<ConflictEntity> {
        var result = emptySpecification<ConflictEntity>()
        if (null == filters) return result
        filters.dateFrom?.also { result = result.and(afterDate(it)) }
        filters.dateTo?.also { result = result.and(beforeDate(it)) }
        filters.fulltext?.also { result = result.and(mentionText(it)) }
        filters.conflictResultIds?.also { result = result.and(matchResults(it)) }
        filters.conflictReasonIds?.also { result = result.and(matchReasons(it)) }
        filters.industryIds?.also { result = result.and(matchIndustries(it)) }
        filters.mainTypeIds?.also { result = result.and(matchMainTypes(it)) }
        filters.ancestorsOf?.also {
            val descendant: ConflictEntity? = conflictRepository.findById(filters.ancestorsOf!!).orElse(null)
            result = if (null == descendant) {
                result.and(falseSpecification<ConflictEntity>())
            } else {
                result.and(ancestorsOf(descendant.treeLeft!!, descendant.treeRight!!))
            }
        }
        filters.childrenOf?.also { result = result.and(childrenOf(it)) }
        filters.near?.also { result = result.and(near(it.lat!!, it.lng!!, it.radius!!)) }
        if (filters.favourites == true && null != userId) {
            result = result.and(favourite(userId))
        }
        return result
    }

    fun afterDate(dateFrom: Int) = Specification<ConflictEntity> { root, _, cb ->
        cb.or(
            cb.greaterThanOrEqualTo(
                root.join<ConflictEntity, EventEntity>("events", JoinType.LEFT)
                    .get<Any>("post")
                    .get("date"),
                LocalDateTime.ofEpochSecond(dateFrom.toLong(), 0, ZoneOffset.UTC)
            )
        )
    }

    fun beforeDate(dateTo: Int) = Specification<ConflictEntity> { root, _, cb ->
        cb.or(
            cb.lessThanOrEqualTo(
                root.join<ConflictEntity, EventEntity>("events", JoinType.LEFT)
                    .get<Any>("post")
                    .get("date"),
                LocalDateTime.ofEpochSecond(dateTo.toLong(), 0, ZoneOffset.UTC)
            )
        )
    }

    fun mentionText(desiredContent: String) = Specification<ConflictEntity> { root, _, cb ->
        if (desiredContent.isBlank()) return@Specification null

        val desiredContentQuery = "%${desiredContent.lowercase()}%"
        val join = root.join<ConflictEntity, EventEntity>("events", JoinType.LEFT)
        cb.or(
            cb.like(cb.lower(root.get("titleRu")), desiredContentQuery),
            cb.like(cb.lower(root.get("titleEn")), desiredContentQuery),
            cb.like(cb.lower(root.get("titleEs")), desiredContentQuery),
            cb.like(cb.lower(root.get("titleDe")), desiredContentQuery),
            cb.like(cb.lower(root.get("companyName")), desiredContentQuery),
            cb.like(cb.lower(join.get<PostEntity>("post").get("titleRu")), desiredContentQuery),
            cb.like(cb.lower(join.get<PostEntity>("post").get("titleEn")), desiredContentQuery),
            cb.like(cb.lower(join.get<PostEntity>("post").get("titleEs")), desiredContentQuery),
            cb.like(cb.lower(join.get<PostEntity>("post").get("titleDe")), desiredContentQuery),
            cb.like(cb.lower(join.get<PostEntity>("post").get("contentRu")), desiredContentQuery),
            cb.like(cb.lower(join.get<PostEntity>("post").get("contentEn")), desiredContentQuery),
            cb.like(cb.lower(join.get<PostEntity>("post").get("contentEs")), desiredContentQuery),
            cb.like(cb.lower(join.get<PostEntity>("post").get("contentDe")), desiredContentQuery)
        )
    }

    fun matchResults(resultIds: List<Long>) = Specification<ConflictEntity> { root, _, cb ->
        cb.`in`(root.get<ConflictResultEntity>("result").get<Any>("id")).value(resultIds)
    }

    fun matchReasons(reasonIds: List<Long>) = Specification<ConflictEntity> { root, _, cb ->
        cb.`in`(root.get<ConflictReasonEntity>("reason").get<Any>("id")).value(reasonIds)
    }

    fun matchIndustries(industryIds: List<Long>) = Specification<ConflictEntity> { root, _, cb ->
        cb.`in`(root.get<IndustryEntity>("industry").get<Any>("id")).value(industryIds)
    }

    fun matchMainTypes(mainTypeIdsAsStrings: List<String>) = Specification<ConflictEntity> { root, _, cb ->
        val mainTypeIds = mainTypeIdsAsStrings
            .map {
                if (it == "null") null
                else it.toLong()
            }
        val orNull = mainTypeIds.contains(null)
        val inPredicate = cb.`in`(root.get<Any>("mainType").get<Any>("id"))
            .value(mainTypeIds.filterNotNull())
        if (orNull) {
            val isNullPredicate = cb.isNull(root.get<Any>("mainType").get<Any>("id"))
            cb.or(inPredicate, isNullPredicate)
        } else inPredicate
    }

    fun ancestorsOf(left: Long, right: Long) = Specification<ConflictEntity> { root, _, cb ->
        cb.and(
            cb.greaterThan(root.get("treeRight"), right),
            cb.lessThan(root.get("treeLeft"), left)
        )
    }

    fun childrenOf(parentId: Long) = Specification<ConflictEntity> { root, _, cb ->
        cb.equal(root.get<Long>("parentId"), parentId)
    }

    fun near(latitude: Float, longitude: Float, radius: Int) = Specification<ConflictEntity> { root, _, cb ->
        cb.lessThanOrEqualTo(
            cb.prod(
                6371.0,
                cb.function(
                    "ACOS",
                    Double::class.java,
                    cb.sum(
                        cb.prod(
                            cb.function(
                                "COS",
                                Double::class.java,
                                cb.function("RADIANS", Double::class.java, cb.literal(latitude))),
                            cb.prod(
                                cb.function(
                                    "COS",
                                    Double::class.java,
                                    cb.function("RADIANS", Double::class.java, root.get<Any>("latitude"))),
                                cb.function(
                                    "COS",
                                    Double::class.java,
                                    cb.diff(
                                        cb.function("RADIANS", Double::class.java, root.get<Any>("longitude")),
                                        cb.function("RADIANS", Double::class.java, cb.literal(longitude)))))),
                        cb.prod(
                            cb.function(
                                "SIN",
                                Double::class.java,
                                cb.function("RADIANS", Double::class.java, cb.literal(latitude))),
                            cb.function(
                                "SIN",
                                Double::class.java,
                                cb.function("RADIANS", Double::class.java, root.get<Any>("latitude"))))))),
            java.lang.Double.valueOf(radius.toDouble())
        )
    }

    fun favourite(userId: Long) = Specification<ConflictEntity> { root, _, cb ->
        cb.`in`(root.join<ConflictEntity, UserEntity>("likedUsers").get<Long>("id")).value(userId)
    }
}
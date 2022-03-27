package ru.smartel.strike.service.event

import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import ru.smartel.strike.dto.request.event.EventFiltersDto
import ru.smartel.strike.entity.ConflictEntity
import ru.smartel.strike.entity.EventEntity
import ru.smartel.strike.entity.PostEntity
import ru.smartel.strike.entity.TagEntity
import ru.smartel.strike.entity.reference.EventStatusEntity
import ru.smartel.strike.entity.reference.EventTypeEntity
import ru.smartel.strike.entity.reference.IndustryEntity
import ru.smartel.strike.repository.conflict.ConflictRepository
import ru.smartel.strike.util.emptySpecification
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class EventFiltersTransformer(
    private val conflictRepository: ConflictRepository,
) {
    fun toSpecification(filters: EventFiltersDto?, userId: Long?): Specification<EventEntity> {
        var result = emptySpecification<EventEntity>()
        if (null == filters) return result

        filters.published?.also { result = result.and(published(it)) }
        filters.dateFrom?.also { result = result.and(afterDate(it)) }
        filters.dateTo?.also { result = result.and(beforeDate(it)) }
        filters.eventStatusIds?.also { result = result.and(matchStatuses(it)) }
        filters.eventTypeIds?.also { result = result.and(matchTypes(it)) }
        filters.industryIds?.also { result = result.and(matchIndustries(it)) }
        filters.tags?.also { result = result.and(withAnyTags(it)) }
        filters.conflictIds?.also {
            //additional query to find ids of parent events of conflicts with given ids
            val parentEventIds = conflictRepository.findAllByIdGetParentEventId(it)
            result = result.and(belongToConflicts(it).or(withIds(parentEventIds)))
        }
        if (filters.favourites == true && userId != null) {
            result = result.and(favourite(userId))
        }
        filters.fulltext?.also { result = result.and(withContent(it)) }
        filters.countryIds?.also { result = result.and(withCountry(it)) }
        filters.regionIds?.also { result = result.and(withRegion(it)) }
        filters.near?.also { result = result.and(near(it.lat!!, it.lat!!, it.radius!!)) }

        return result
    }

    fun published(published: Boolean) = Specification<EventEntity> { root, _, cb ->
        cb.equal(root.get<PostEntity>("post").get<Boolean>("published"), published)
    }

    fun afterDate(dateFrom: Int) = Specification<EventEntity> { root, _, cb ->
        cb.greaterThanOrEqualTo(
            root.get<PostEntity>("post").get("date"),
            LocalDateTime.ofEpochSecond(dateFrom.toLong(), 0, ZoneOffset.UTC)
        )
    }

    fun beforeDate(dateTo: Int) = Specification<EventEntity> { root, _, cb ->
        cb.lessThanOrEqualTo(
            root.get<PostEntity>("post").get("date"),
            LocalDateTime.ofEpochSecond(dateTo.toLong(), 0, ZoneOffset.UTC)
        )
    }

    fun matchStatuses(statusIds: List<Long>) = Specification<EventEntity> { root, _, cb ->
        cb.`in`(root.get<EventStatusEntity>("status").get<Any>("id")).value(statusIds)
    }

    fun matchTypes(typeIds: List<Long>) = Specification<EventEntity> { root, _, cb ->
        cb.`in`(root.get<EventTypeEntity>("type").get<Any>("id")).value(typeIds)
    }

    fun matchIndustries(industryIds: List<Long>) = Specification<EventEntity> { root, _, cb ->
        cb.`in`(root.get<ConflictEntity>("conflict").get<IndustryEntity>("industry").get<List<Long>>("id"))
            .value(industryIds)
    }

    fun withAnyTags(tags: List<String>) = Specification<EventEntity> { root, _, cb ->
        val `in` = cb.`in`<String>(root.join<EventEntity, TagEntity>("tags").get("name"))
        tags.forEach { `in`.value(it) }
        `in`
    }

    fun belongToConflicts(conflictIds: List<Long>) = Specification<EventEntity> { root, _, cb ->
        //Events which belong to conflicts with given ids
        cb.`in`(root.get<ConflictEntity>("conflict").get<List<Long>>("id")).value(conflictIds)
    }

    fun withIds(ids: List<Long>) = Specification<EventEntity> { root, _, cb ->
        //With parent-events of conflicts with given ids
        cb.`in`(root.get<Any>("id")).value(ids)
    }

    fun favourite(userId: Long) = Specification<EventEntity> { root, _, cb ->
        cb.`in`(root.join<Any, Any>("likedUsers").get<Any>("id")).value(userId)
    }

    fun withContent(desiredContent: String) = Specification<EventEntity> { root, _, cb ->
        val desiredContentQuery = "%$desiredContent%"
        cb.or(
            cb.like(cb.lower(root.get<Any>("post").get("titleRu")), desiredContentQuery),
            cb.like(cb.lower(root.get<Any>("post").get("titleEn")), desiredContentQuery),
            cb.like(cb.lower(root.get<Any>("post").get("titleEs")), desiredContentQuery),
            cb.like(cb.lower(root.get<Any>("post").get("titleDe")), desiredContentQuery),
            cb.like(cb.lower(root.get<Any>("post").get("contentRu")), desiredContentQuery),
            cb.like(cb.lower(root.get<Any>("post").get("contentEn")), desiredContentQuery),
            cb.like(cb.lower(root.get<Any>("post").get("contentEs")), desiredContentQuery),
            cb.like(cb.lower(root.get<Any>("post").get("contentDe")), desiredContentQuery)
        )
    }

    fun withCountry(countryIds: List<Long>) = Specification<EventEntity> { root, _, cb ->
        cb.`in`(
            root
                .join<Any, Any>("locality")
                .join<Any, Any>("region")
                .get<Any>("country")
                .get<Any>("id")
        ).value(countryIds)
    }

    fun withRegion(regionIds: List<Long>) = Specification<EventEntity> { root, _, cb ->
        cb.`in`(
            root
                .join<Any, Any>("locality")
                .get<Any>("region")
                .get<Any>("id")
        ).value(regionIds)
    }

    fun near(latitude: Float, longitude: Float, radius: Int) = Specification<EventEntity> { root, _, cb ->
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
}
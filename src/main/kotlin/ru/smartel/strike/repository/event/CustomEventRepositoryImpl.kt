package ru.smartel.strike.repository.event

import org.springframework.data.jpa.domain.Specification
import ru.smartel.strike.dto.service.event.type.EventTypeCountDto
import ru.smartel.strike.dto.service.sort.event.EventSortDto
import ru.smartel.strike.entity.ConflictEntity
import ru.smartel.strike.entity.EventEntity
import java.math.BigInteger
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Root

class CustomEventRepositoryImpl(
    @PersistenceContext private val entityManager: EntityManager
) : CustomEventRepository {
    override fun isNotParentForAnyConflicts(eventId: Long): Boolean {
        val count = entityManager.createQuery(
            "select count(c.id) from ${ConflictEntity::class.java.name} c where c.parentEvent = $eventId"
        )
            .setMaxResults(1)
            .singleResult as Long
        return (count == 0L)
    }

    override fun findIds(
        specification: Specification<EventEntity>,
        sortDTO: EventSortDto,
        page: Int,
        perPage: Int
    ): List<Long> {
        val cb = entityManager.criteriaBuilder
        val idQuery = cb.createQuery(Long::class.java)
        val root: Root<EventEntity> = idQuery.from(EventEntity::class.java)
        idQuery.select(root.get("id"))
            .where(specification.toPredicate(root, idQuery, cb))
            .orderBy(sortDTO.toOrder(cb, root))

        return entityManager.createQuery(idQuery)
            .setMaxResults(perPage)
            .setFirstResult((page - 1) * perPage)
            .resultList
    }

    override fun getEventTypesCountByConflictId(conflictId: Long): List<EventTypeCountDto> {
        val countByTypeId = entityManager.createNativeQuery(
            "select event_type_id, count, priority" +
                    " from (select e.event_type_id, count(e.id) count" +
                    "      from events e" +
                    "      where e.conflict_id = :conflictId" +
                    "        and e.event_type_id is not null" +
                    "      group by e.event_type_id) sub" +
                    "         left join event_types et on et.id = event_type_id"
        )
            .setParameter("conflictId", conflictId)
            .resultList as List<Array<Any>>
        return countByTypeId
            .map { raw ->
                EventTypeCountDto(
                    (raw!![0] as Int).toLong(), (raw[1] as BigInteger).toLong(), raw[2] as Int
                )
            }
    }

    override fun incrementViews(ids: Collection<Long>) {
        entityManager.createNativeQuery("update events set views = views + 1 where id in :ids")
            .setParameter("ids", ids)
            .executeUpdate()
    }
}
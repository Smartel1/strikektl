package ru.smartel.strike.repository.event

import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.smartel.strike.dto.service.event.type.EventTypeCountDto
import ru.smartel.strike.dto.service.sort.event.EventSortDto
import ru.smartel.strike.entity.EventEntity

@Repository
interface CustomEventRepository {
    fun isNotParentForAnyConflicts(eventId: Long): Boolean

    fun findIds(specification: Specification<EventEntity>, sortDTO: EventSortDto, page: Int, perPage: Int): List<Long>

    /**
     * Count of each event types for specified [conflictId]. Null-types ignored
     */
    fun getEventTypesCountByConflictId(conflictId: Long): List<EventTypeCountDto>

    fun incrementViews(ids: Collection<Long>)
}
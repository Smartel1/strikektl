package ru.smartel.strike.service.conflict

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.repository.conflict.ConflictRepository
import ru.smartel.strike.repository.event.EventRepository
import ru.smartel.strike.repository.event.EventTypeRepository

@Service
@Transactional(rollbackFor = [Exception::class])
class ConflictMainTypeService(
    private val conflictRepository: ConflictRepository,
    private val eventRepository: EventRepository,
    private val eventTypeRepository: EventTypeRepository
) {
    companion object {
        /**
         * count of events with one type needed to set this type to conflict
         */
        const val EVENT_TYPE_THRESHOLD = 20
    }

    /**
     * Recalculate main conflict type (if this property is automanaged)
     *
     * @param conflictId conflict id
     */
    fun refreshMainType(conflictId: Long) {
        val conflict = conflictRepository.findByIdOrNull(conflictId)!!
        if (!conflict.automanagingMainType) {
            return
        }
        val newTypeId = calculateMainType(conflictId)
        conflict.mainType = newTypeId?.let { eventTypeRepository.getById(it) }
    }


    /**
     * Calculate main type of conflict depending on it's events' types
     *
     * @param conflictId conflict id
     * @return main type id or null, if cannot calculate
     */
    private fun calculateMainType(conflictId: Long): Long? {
        val presentTypes = eventRepository.getEventTypesCountByConflictId(conflictId)
        if (presentTypes.isEmpty()) {
            return null
        }

        // if all events has the same type, then set this type to conflict
        if (presentTypes.size == 1) {
            return presentTypes[0].typeId
        }

        // If more than EVENT_TYPE_THRESHOLD events has the same type, then set this type to conflict.
        // But if 2 or more type are present in more than EVENT_TYPE_THRESHOLD events, then set null.
        val dominatingTypes = presentTypes
            .filter { it.eventsCount >= EVENT_TYPE_THRESHOLD }
        if (dominatingTypes.isNotEmpty()) {
            return if (dominatingTypes.size == 1) dominatingTypes[0].typeId
            else null
        }

        // If one of the types present with maximum events count and has maximum priority - it wins
        val maxTypePriority: Int = presentTypes.stream()
            .map { it.priority }
            .max { i1, i2 -> i1.compareTo(i2) }
            .orElseThrow { IllegalStateException("type priority cannot be null") }

        if (maxTypePriority == 0) {
            return null
        }
        val maxEventsCount: Long = presentTypes.stream()
            .map { it.eventsCount }
            .max { i1, i2 -> i1.compareTo(i2) }
            .orElseThrow { IllegalStateException("events count cannot be null") }

        val winType = presentTypes.stream()
            .filter { (_, eventsCount) -> eventsCount == maxEventsCount }
            .filter { (_, _, priority) -> priority == maxTypePriority }
            .findFirst()
        return winType.orElse(null)?.typeId
    }
}
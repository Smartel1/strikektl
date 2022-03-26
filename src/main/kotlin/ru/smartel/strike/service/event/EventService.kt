package ru.smartel.strike.service.event

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.entity.EventEntity
import ru.smartel.strike.entity.PostEntity
import ru.smartel.strike.entity.interfaces.Post
import ru.smartel.strike.entity.reference.EventStatusEntity
import ru.smartel.strike.repository.conflict.ConflictRepository
import ru.smartel.strike.repository.event.EventRepository
import ru.smartel.strike.repository.event.EventStatusRepository
import java.lang.Exception

@Service
@Transactional(rollbackFor = [Exception::class])
class EventService(
    private val eventRepository: EventRepository,
    private val eventStatusRepository: EventStatusRepository,
    private val conflictRepository: ConflictRepository
) {
    fun updateConflictsEventStatuses(conflictId: Long) {
        val conflict = conflictRepository.findById(conflictId)
            .orElseThrow { IllegalStateException("Cannot update events of unknown conflict") }

        val events = eventRepository.findAllByConflictId(conflictId)
            .sortedBy { it.post.date }

        if (events.isEmpty()) return
        //first event is 'new' event (unless it's final one in the same moment)
        //first event is 'new' event (unless it's final one in the same moment)
        events[0].status = eventStatusRepository.findFirstBySlug(EventStatusEntity.NEW)

        //every 1..n event is 'intermediate'
        if (events.size > 1) {
            val intermediateStatus = eventStatusRepository.findFirstBySlug(EventStatusEntity.INTERMEDIATE)
            for (i in 1 until events.size) {
                events[i].status  = intermediateStatus
            }
        }

        conflict.dateTo?.also {
            //latest event of finished conflict is 'final' event
            events[events.size - 1].status  = eventStatusRepository.findFirstBySlug(EventStatusEntity.FINAL)
        }
    }
}
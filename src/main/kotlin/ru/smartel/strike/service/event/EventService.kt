package ru.smartel.strike.service.event

import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.dto.request.event.EventListRequestDto
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.response.ListWrapperDtoMeta
import ru.smartel.strike.dto.response.TitlesDto
import ru.smartel.strike.dto.response.conflict.BriefConflictWithEventsDto
import ru.smartel.strike.dto.response.event.BriefEventDto
import ru.smartel.strike.dto.response.event.EventDetailDto
import ru.smartel.strike.dto.response.event.EventListDto
import ru.smartel.strike.dto.service.sort.event.EventSortDto
import ru.smartel.strike.entity.EventEntity
import ru.smartel.strike.entity.reference.EventStatusEntity
import ru.smartel.strike.repository.conflict.ConflictRepository
import ru.smartel.strike.repository.event.EventRepository
import ru.smartel.strike.repository.event.EventStatusRepository
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.ListRequestValidator
import ru.smartel.strike.service.Locale
import java.time.ZoneOffset
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@Transactional(rollbackFor = [Exception::class])
class EventService(
    private val eventDtoValidator: EventDtoValidator,
    private val listRequestValidator: ListRequestValidator,
    private val eventFiltersTransformer: EventFiltersTransformer,
    private val eventRepository: EventRepository,
    private val eventStatusRepository: EventStatusRepository,
    private val conflictRepository: ConflictRepository
) {
    @PreAuthorize("permitAll()")
    fun list(
        dto: EventListRequestDto,
        listRequest: BaseListRequestDto,
        locale: Locale,
        user: UserPrincipal?
    ): ListWrapperDto<EventListDto> {
        listRequestValidator.validateListQueryDTO(
            listRequest,
            availableSortFields = listOf("createdAt", "date", "views")
        )
        eventDtoValidator.validateListRequestDto(dto)

        val specification = eventFiltersTransformer.toSpecification(dto.filters, user?.id)
            .and { root, _, cb ->
                if (user == null || !user.canModerate()) {
                    cb.equal(root.get<Any>("post").get<Any>("published"), true)
                } else null
            }
            .and { root, _, cb ->
                if (locale != Locale.ALL) {
                    cb.and(
                        cb.isNotNull(root.get<Any>("post").get<Any>("title" + locale.pascalCase())),
                        cb.isNotNull(root.get<Any>("post").get<Any>("content" + locale.pascalCase()))
                    )
                } else null
            }
        //Get count of events matching specification
        val eventsCount = eventRepository.count(specification)

        val responseMeta = ListWrapperDtoMeta(eventsCount, listRequest.page, listRequest.perPage)

        if (eventsCount <= (listRequest.page - 1) * listRequest.perPage) {
            return ListWrapperDto(emptyList(), responseMeta)
        }
        val sortDto = EventSortDto.of(listRequest.sort)

        //Get count of events matching specification. Because pagination and fetching dont work together
        val ids = eventRepository.findIds(specification, sortDto, listRequest.page, listRequest.perPage);
        val eventListDTOs = eventRepository.findAllById(ids).asSequence()
            .sortedWith(sortDto.toComparator())
            .map { EventListDto(it, locale) }
            .toList()
        eventRepository.incrementViews(eventListDTOs.map { it.post.id })

        return ListWrapperDto(eventListDTOs, responseMeta)
    }

    @PreAuthorize("permitAll()")
    @PostAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or returnObject.postDetails.published")
    fun incrementViewsAndGet(id: Long, locale: Locale, withRelatives: Boolean, user: UserPrincipal?): EventDetailDto {
        val event = getByIdOrThrow(id)
        event.post.views++
        return EventDetailDto(
            event = event,
            locale = locale,
            relatives = if (withRelatives) getRelatives(event, locale, user?.canModerate() == true)
            else null
        )
    }

    /**
     * Get related events grouped by containing conflicts.
     * Related means they belongs to same root conflict
     */
    private fun getRelatives(
        event: EventEntity,
        locale: Locale,
        includeNotPublished: Boolean
    ): List<BriefConflictWithEventsDto> {
        val conflict = event.conflict ?: return listOf()

        //get root conflict
        val rootConflict = conflictRepository.getRootConflict(conflict)

        //get all conflicts caused by root
        val conflictsOfRoot = conflictRepository.getDescendantsAndSelf(rootConflict)

        //Create list of conflict DTOs. Each containing its events list

        //Create list of conflict DTOs. Each containing its events list
        return conflictsOfRoot
            .map { conf ->
                BriefConflictWithEventsDto(
                    id = conf.id,
                    parentEventId = conf.parentEvent?.id,
                    parentConflictId = conf.parentId,
                    events = conf.events.asSequence()
                        .filter { includeNotPublished || it.post.published }
                        .map {
                            BriefEventDto(
                                id = it.id,
                                date = it.post.date.toEpochSecond(ZoneOffset.UTC),
                                titles = TitlesDto.withDefaultRuLocale(it, locale)
                            )
                        }
                        .sortedWith(Comparator.comparingLong { it.date })
                        .toList()
                )
            }
    }

    private fun getByIdOrThrow(id: Long) = eventRepository.findById(id)
        .orElseThrow { EntityNotFoundException("Событие не найдено") }

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
                events[i].status = intermediateStatus
            }
        }

        conflict.dateTo?.also {
            //latest event of finished conflict is 'final' event
            events[events.size - 1].status = eventStatusRepository.findFirstBySlug(EventStatusEntity.FINAL)
        }
    }
}
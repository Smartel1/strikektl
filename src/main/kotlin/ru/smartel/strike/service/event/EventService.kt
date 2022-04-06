package ru.smartel.strike.service.event

import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.dto.request.event.EventCreateRequestDto
import ru.smartel.strike.dto.request.event.EventListRequestDto
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.response.ListWrapperDtoMeta
import ru.smartel.strike.dto.response.TitlesDto
import ru.smartel.strike.dto.response.conflict.BriefConflictWithEventsDto
import ru.smartel.strike.dto.response.event.BriefEventDto
import ru.smartel.strike.dto.response.event.EventDetailDto
import ru.smartel.strike.dto.response.event.EventListDto
import ru.smartel.strike.dto.service.sort.event.EventSortDto
import ru.smartel.strike.entity.*
import ru.smartel.strike.entity.reference.EventStatusEntity
import ru.smartel.strike.repository.conflict.ConflictRepository
import ru.smartel.strike.repository.etc.*
import ru.smartel.strike.repository.event.EventRepository
import ru.smartel.strike.repository.event.EventStatusRepository
import ru.smartel.strike.repository.event.EventTypeRepository
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.ListRequestValidator
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.conflict.ConflictMainTypeService
import ru.smartel.strike.service.notifications.PushService
import ru.smartel.strike.service.publish.PostPublicationService
import ru.smartel.strike.service.user.UserService
import ru.smartel.strike.util.validate
import ru.smartel.strike.util.validation.rules.EventAfterConflictsStart
import ru.smartel.strike.util.validation.rules.EventBeforeConflictsEnd
import ru.smartel.strike.util.validation.rules.UserCanModerate
import java.time.LocalDateTime
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
    private val eventTypeRepository: EventTypeRepository,
    private val conflictRepository: ConflictRepository,
    private val localityRepository: LocalityRepository,
    private val photoRepository: PhotoRepository,
    private val videoRepository: VideoRepository,
    private val videoTypeRepository: VideoTypeRepository,
    private val tagRepository: TagRepository,
    private val userService: UserService,
    private val conflictMainTypeService: ConflictMainTypeService,
    private val pushService: PushService,
    private val postPublicationService: PostPublicationService
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
        val ids = eventRepository.findIds(specification, sortDto, listRequest.page, listRequest.perPage)
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

    @PreAuthorize("isFullyAuthenticated()")
    fun setFavourite(id: Long, userId: Long, isFavourite: Boolean) {
        val user = userService.getByIdOrThrow(userId)
        val event = eventRepository.getById(id)

        val currentFavourites = user.favouriteEvents

        if (isFavourite) {
            //If not in favourites - add it
            if (!currentFavourites.contains(event)) {
                currentFavourites.add(event)
            }
        } else {
            currentFavourites.remove(event)
        }
    }

    @PreAuthorize("isFullyAuthenticated()")
    fun create(dto: EventCreateRequestDto, locale: Locale, user: UserPrincipal?): EventDetailDto {
        eventDtoValidator.validateStoreDto(dto)

        //Only moderator can publish events
        validate(
            UserCanModerate(
                user!!,
                condition = dto.published?.orElse(false) == true || dto.localityId?.isPresent == true
            )
        )

        val event = createEvent(dto, locale, user.id)

        validate(
            EventBeforeConflictsEnd(event.post.date, event.conflict.dateTo),
            EventAfterConflictsStart(event.post.date, event.conflict.dateFrom)
        )

        updateConflictsEventStatuses(event.conflict.id)
        conflictMainTypeService.refreshMainType(event.conflict.id)

        //Send push
        if (!event.post.published) {
            //if event is created non published - notify moderators
            pushService.eventCreatedByUser(event.id, event.post.author.id, event.post.author.name);
        } else {
            //if event published - notify subscribers and send to networks
            postPublicationService.publishAndSetFlags(event, dto.publishTo)

            if (dto.pushRequired) {
                val titlesByLocales = Locale.values().asSequence()
                    .filter { it != Locale.ALL }
                    .associateWith { event.getTitleByLocale(it) }
                    .filterValues { it != null } as Map<Locale, String>


                event.post.setPushFlagsForLocales(titlesByLocales.keys)
                pushService.eventPublished(
                    event.id,
                    event.post.author.id,
                    event.longitude,
                    event.latitude,
                    Optional.ofNullable(event.type).orElse(null)?.id,
                    Optional.ofNullable(event.locality).orElse(null)?.region?.id,
                    titlesByLocales,
                    null, //do not send push to author cuz he's moderator
                    false
                )
            }
        }
        return EventDetailDto(event, locale)
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
        val conflict = event.conflict

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

    private fun createEvent(dto: EventCreateRequestDto, locale: Locale, authorId: Long): EventEntity {
        //for the sake of PATCH ;)
        val event = EventEntity(
            conflict = conflictRepository.getById(dto.conflictId!!.get()),
            post = PostEntity(
                date = LocalDateTime.ofEpochSecond(dto.date!!.get(), 0, ZoneOffset.UTC),
                sourceLink = dto.sourceLink?.orElse(null)?.trim(),
                titleRu = dto.titleRu?.orElse(null)?.trim(),
                titleEn = dto.titleEn?.orElse(null)?.trim(),
                titleEs = dto.titleEs?.orElse(null)?.trim(),
                titleDe = dto.titleDe?.orElse(null)?.trim(),
                contentRu = dto.contentRu?.orElse(null)?.trim(),
                contentEn = dto.contentEn?.orElse(null)?.trim(),
                contentEs = dto.contentEs?.orElse(null)?.trim(),
                contentDe = dto.contentDe?.orElse(null)?.trim(),
                published = dto.published?.orElseThrow() ?: false,
                author = userService.getByIdOrThrow(authorId)
            ),
            latitude = dto.latitude!!.orElseThrow(),
            longitude = dto.longitude!!.orElseThrow(),
            locality = dto.localityId?.orElse(null)?.let { localityRepository.getById(it) },
            type = dto.eventTypeId?.orElse(null)?.let { eventTypeRepository.getById(it) },
            photos = dto.photoUrls?.orElseThrow()
                ?.map { PhotoEntity(url = it.trim()) }
                ?.onEach { photoRepository.save(it) }
                ?.toSet() ?: emptySet(),
            videos = dto.videos?.orElseThrow()
                ?.map {
                    VideoEntity(
                        url = it.url!!.trim(),
                        previewUrl = it.previewUrl?.orElse(null)?.trim(),
                        videoType = videoTypeRepository.getById(it.videoTypeId!!)
                    )
                }
                ?.onEach { videoRepository.save(it) }
                ?.toSet() ?: emptySet(),
            tags = dto.tags?.orElseThrow()
                ?.map { it.trim() }
                ?.map { name ->
                    tagRepository.findFirstByName(name)
                        .orElseGet { tagRepository.save(TagEntity(name = name)) }
                }
                ?.toSet() ?: emptySet(),
            likedUsers = emptySet()
        )
        dto.title?.also {
            event.setTitleByLocale(locale, it.orElse(null)?.trim())
        }
        dto.content?.also {
            event.setContentByLocale(locale, it.orElse(null)?.trim())
        }
        eventRepository.save(event)
        return event
    }
}
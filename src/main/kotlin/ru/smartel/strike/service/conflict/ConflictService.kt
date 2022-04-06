package ru.smartel.strike.service.conflict

import org.springframework.data.jpa.domain.Specification
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.dto.request.conflict.ConflictFieldsDto
import ru.smartel.strike.dto.request.conflict.ConflictListRequestDto
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.response.ListWrapperDtoMeta
import ru.smartel.strike.dto.response.TitlesDto
import ru.smartel.strike.dto.response.conflict.ConflictDetailDto
import ru.smartel.strike.dto.response.conflict.ConflictListDto
import ru.smartel.strike.dto.response.conflict.ConflictReportDto
import ru.smartel.strike.dto.response.conflict.FullConflictDto
import ru.smartel.strike.dto.response.reference.country.CountryDetailDto
import ru.smartel.strike.dto.response.reference.locality.LocalityDto
import ru.smartel.strike.dto.response.reference.region.RegionDto
import ru.smartel.strike.dto.service.sort.conflict.ConflictSortDto
import ru.smartel.strike.entity.ConflictEntity
import ru.smartel.strike.exception.ValidationException
import ru.smartel.strike.repository.conflict.ConflictReasonRepository
import ru.smartel.strike.repository.conflict.ConflictRepository
import ru.smartel.strike.repository.conflict.ConflictResultRepository
import ru.smartel.strike.repository.etc.IndustryRepository
import ru.smartel.strike.repository.etc.UserRepository
import ru.smartel.strike.repository.event.EventRepository
import ru.smartel.strike.repository.event.EventTypeRepository
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.ListRequestValidator
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.event.EventService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@Transactional(rollbackFor = [Exception::class])
class ConflictService(
    private val dtoValidator: ConflictDtoValidator,
    private val listRequestValidator: ListRequestValidator,
    private val filtersTransformer: ConflictFiltersTransformer,
    private val conflictRepository: ConflictRepository,
    private val conflictReasonRepository: ConflictReasonRepository,
    private val conflictResultRepository: ConflictResultRepository,
    private val industryRepository: IndustryRepository,
    private val eventRepository: EventRepository,
    private val eventService: EventService,
    private val eventTypeRepository: EventTypeRepository,
    private val userRepository: UserRepository,
) {

    @PreAuthorize("permitAll()")
    fun list(
        conflictsRequest: ConflictListRequestDto,
        listRequest: BaseListRequestDto,
        locale: Locale,
        user: UserPrincipal?
    ): ListWrapperDto<ConflictListDto> {
        dtoValidator.validateListRequestDto(conflictsRequest)
        listRequestValidator.validateListQueryDTO(listRequest)

        // Transform filters and other restrictions to Specifications
        val specification: Specification<ConflictEntity> = filtersTransformer
            .toSpecification(conflictsRequest.filters, user?.id)
            .and { root, _, cb ->
                if (Locale.ALL != locale) {
                    cb.isNotNull(root.get<String>("title" + locale.pascalCase()))
                } else null
            }

        // Get count of conflicts matching specification
        val conflictsCount = conflictRepository.count(specification)
        val responseMeta = ListWrapperDtoMeta(
            total = conflictsCount,
            currentPage = listRequest.page,
            perPage = listRequest.perPage
        )

        if (conflictsCount <= (listRequest.page - 1) * listRequest.perPage) {
            return ListWrapperDto(emptyList(), responseMeta)
        }

        val sortDTO: ConflictSortDto = ConflictSortDto.of(listRequest.sort)

        // Get count of conflicts matching specification. Because pagination and fetching dont work together
        val ids =
            conflictRepository.findIds(specification, sortDTO, listRequest.page, listRequest.perPage)

        val conflictListDTOS: List<ConflictListDto> = conflictRepository.findAllById(ids).asSequence()
            .sortedWith(sortDTO.toComparator())
            .map {
                ConflictListDto(
                    id = it.id,
                    titles = TitlesDto(it, locale),
                    fullConflictDto = if (!conflictsRequest.brief) FullConflictDto(it)
                    else null
                )
            }
            .toList()

        return ListWrapperDto(conflictListDTOS, responseMeta)
    }

    @PreAuthorize("permitAll()")
    fun get(conflictId: Long, locale: Locale): ConflictDetailDto {
        return ConflictDetailDto(findByIdOrThrow(conflictId), locale)
    }

    @PreAuthorize("isFullyAuthenticated()")
    fun setFavourite(conflictId: Long, userId: Long, isFavourite: Boolean) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalStateException("Authorization cannot pass empty user into this method") }
        val conflict = conflictRepository.getById(conflictId)
        val currentFavourites = user.favouriteConflicts
        if (isFavourite) {
            //If not in favourites - add it
            if (!currentFavourites.contains(conflict)) {
                currentFavourites.add(conflict)
            }
        } else {
            currentFavourites.remove(conflict)
        }
    }

    @PreAuthorize("isAuthenticated()")
    fun getReportByPeriod(from: LocalDate, to: LocalDate, countriesIds: List<Long>): ConflictReportDto {
        return ConflictReportDto(
            conflictsBeganBeforeDateFromCount = conflictRepository.getOldConflictsCount(from, to, countriesIds),
            countByCountries = conflictRepository.getCountByCountries(from, to, countriesIds),
            countByDistricts = conflictRepository.getCountByDistricts(from, to, countriesIds),
            countByRegions = conflictRepository.getCountByRegions(from, to, countriesIds),
            specificCountByDistricts = conflictRepository.getSpecificCountByDistricts(from, to, countriesIds),

            countByIndustries = conflictRepository.getCountByIndustries(from, to, countriesIds),
            countByReasons = conflictRepository.getCountByReasons(from, to, countriesIds),
            countByResults = conflictRepository.getCountByResults(from, to, countriesIds),
            countByTypes = conflictRepository.getCountByTypes(from, to, countriesIds),

            countByResultsByTypes = conflictRepository.getCountByResultsByTypes(from, to, countriesIds),
            countByResultsByIndustries = conflictRepository.getCountByResultsByIndustries(from, to, countriesIds),
            countByReasonsByIndustries = conflictRepository.getCountByReasonsByIndustries(from, to, countriesIds),
            countByTypesByIndustries = conflictRepository.getCountPercentByTypesByIndustries(from, to, countriesIds)
        )
    }

    @PreAuthorize("permitAll()")
    fun getLatestLocality(conflictId: Long, locale: Locale): LocalityDto {
        findByIdOrThrow(conflictId)
        return eventRepository.findFirstByConflictIdAndLocalityNotNullOrderByPostDateDesc(conflictId)
            ?.let { LocalityDto(it.locality!!, locale) }
            ?: throw EntityNotFoundException("У событий запрошенного конфликта не найдено населенных пунктов")
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun create(dto: ConflictFieldsDto, locale: Locale, user: UserPrincipal?): ConflictDetailDto {
        dtoValidator.validateConflictFieldsDtoForCreate(dto)
        val conflict = ConflictEntity()
        fillConflictFields(conflict, dto, locale)

        val parentConflict = conflict.parentEvent?.conflict
        conflictRepository.insertAsLastChildOf(conflict, parentConflict)
        return ConflictDetailDto(conflict, locale)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun update(id: Long, dto: ConflictFieldsDto, locale: Locale, user: UserPrincipal?): ConflictDetailDto {
        dtoValidator.validateConflictFieldsDtoForUpdate(dto)

        eventRepository.findFirstByConflictIdOrderByPostDateDesc(id)?.also { latestEvent ->
            if (dto.dateTo?.isPresent == true && latestEvent.post.date.toEpochSecond(UTC) > dto.dateTo!!.get()) {
                throw ValidationException(
                    mapOf("dateTo" to listOf("конфликт не должен кончаться раньше последнего события"))
                )
            }
            if (dto.dateFrom?.isPresent == true && latestEvent.post.date.toEpochSecond(UTC) < dto.dateFrom!!.get()) {
                throw ValidationException(
                    mapOf("dateFrom" to listOf("конфликт не должен начинаться позже первого события"))
                )
            }
        }

        val conflict = findByIdOrThrow(id)
        val dateToBeforeUpdate = conflict.dateTo

        fillConflictFields(conflict, dto, locale)

        val parentConflict = conflict.parentEvent?.conflict

        conflictRepository.insertAsLastChildOf(conflict, parentConflict)

        // If dateTo going to be changed - update events' statuses
        if (!Objects.equals(dateToBeforeUpdate, conflict.dateTo)) {
            eventService.updateConflictsEventStatuses(conflict.id)
        }

        return ConflictDetailDto(conflict, locale)
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun delete(id: Long) {
        val conflict = findByIdOrThrow(id)
        check(!conflictRepository.hasChildren(conflict)) {
            "В текущей реализации нельзя удалять конфликты, у которых есть потомки"
        }
        conflictRepository.deleteFromTree(conflict)
    }

    private fun fillConflictFields(conflict: ConflictEntity, dto: ConflictFieldsDto, locale: Locale) {
        //for the sake of PATCH ;)
        dto.title?.also { conflict.setTitleByLocale(locale, dto.title?.orElse(null)) }
        dto.titleRu?.also { conflict.titleRu = dto.titleRu?.orElse(null) }
        dto.titleEn?.also { conflict.titleEn = dto.titleEn?.orElse(null) }
        dto.titleEs?.also { conflict.titleEs = dto.titleEs?.orElse(null) }
        dto.titleDe?.also { conflict.titleDe = dto.titleDe?.orElse(null) }
        dto.latitude?.also { conflict.latitude = dto.latitude!! }
        dto.longitude?.also { conflict.longitude = dto.longitude!! }
        dto.companyName?.also { conflict.companyName = dto.companyName?.orElse(null) }
        dto.dateFrom?.also {
            conflict.dateFrom = dto.dateFrom!!
                .map { LocalDateTime.ofEpochSecond(it, 0, UTC) }
                .orElse(null)
        }
        dto.dateTo?.also {
            conflict.dateTo = dto.dateTo!!
                .map { LocalDateTime.ofEpochSecond(it, 0, UTC) }
                .orElse(null)
        }
        dto.conflictReasonId?.also {
            conflict.reason = dto.conflictReasonId!!
                .map { conflictReasonRepository.getById(it) }
                .orElse(null)
        }
        dto.conflictResultId?.also {
            conflict.result = dto.conflictResultId!!
                .map { conflictResultRepository.getById(it) }
                .orElse(null)
        }
        dto.industryId?.also {
            conflict.industry = dto.industryId!!
                .map { industryRepository.getById(it) }
                .orElse(null)
        }
        dto.parentEventId?.also {
            conflict.parentEvent = dto.parentEventId!!
                .map { eventRepository.getById(it) }
                .orElse(null)
        }
        dto.mainTypeId?.also {
            conflict.mainType = dto.mainTypeId!!
                .map { eventTypeRepository.getById(it) }
                .orElse(null)
            // if once set main type manually, automanaging is disabling
            conflict.automanagingMainType = false
        }
        dto.automanagingMainType?.also {
            conflict.automanagingMainType = dto.automanagingMainType!!
        }
    }

    public fun findByIdOrThrow(id: Long) = conflictRepository.findById(id)
        .orElseThrow { EntityNotFoundException("Конфликт не найден") }!!
}
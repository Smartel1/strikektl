package ru.smartel.strike.service.conflict

import org.springframework.data.jpa.domain.Specification
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.dto.request.conflict.ConflictListRequestDto
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.response.ListWrapperDtoMeta
import ru.smartel.strike.dto.response.TitlesDto
import ru.smartel.strike.dto.response.conflict.ConflictListDto
import ru.smartel.strike.dto.response.conflict.FullConflictDto
import ru.smartel.strike.dto.service.sort.ConflictSortDto
import ru.smartel.strike.entity.ConflictEntity
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
//    private val eventService: EventService,
    private val eventTypeRepository: EventTypeRepository,
    private val userRepository: UserRepository,
) {

    @PreAuthorize("permitAll()")
    fun list(conflictsRequest: ConflictListRequestDto, listRequest: BaseListRequestDto, user: UserPrincipal?):
            ListWrapperDto<ConflictListDto> {
        dtoValidator.validateListQueryDTO(conflictsRequest)
        listRequestValidator.validateListQueryDTO(listRequest)

        // Transform filters and other restrictions to Specifications
        val specification: Specification<ConflictEntity> = filtersTransformer
            .toSpecification(conflictsRequest.filters, user?.id)
            .and { root, _, cb ->
                if (Locale.ALL != conflictsRequest.locale) {
                    cb.isNotNull(root.get<String>("title" + conflictsRequest.locale!!.pascalCase()))
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
                    titles = TitlesDto(it, conflictsRequest.locale!!),
                    fullConflictDto = if (!conflictsRequest.brief) FullConflictDto(it)
                    else null
                )
            }
            .toList()

        return ListWrapperDto(conflictListDTOS, responseMeta)
    }
}
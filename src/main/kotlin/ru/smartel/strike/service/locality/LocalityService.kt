package ru.smartel.strike.service.locality

import org.springframework.data.jpa.domain.Specification
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.request.reference.locality.LocalityCreateRequestDto
import ru.smartel.strike.dto.response.reference.locality.LocalityDetailDto
import ru.smartel.strike.entity.reference.LocalityEntity
import ru.smartel.strike.repository.etc.LocalityRepository
import ru.smartel.strike.repository.etc.RegionRepository
import ru.smartel.strike.service.Locale

@Service
@Transactional(rollbackFor = [Exception::class])
class LocalityService(
    private val localityRepository: LocalityRepository,
    private val regionRepository: RegionRepository,
    private val validator: LocalityDtoValidator,
) {
    fun list(name: String?, regionId: Int?, locale: Locale): ListWrapperDto<LocalityDetailDto> {
        val localities = localityRepository.findAll(localityOfRegionSpec(regionId).and(namePatternLocalitySpec(name)))
        return ListWrapperDto(localities.asSequence()
            .map { LocalityDetailDto(it, locale) }
            .sortedBy { it.name }
            .toList())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun create(dto: LocalityCreateRequestDto): LocalityDetailDto {
        validator.validateUpdateDTO(dto)
        return LocalityEntity()
            .apply {
                name = dto.name!!
                region = regionRepository.getById(dto.regionId!!)
            }
            .also { localityRepository.save(it) }
            .let { LocalityDetailDto(it, dto.locale!!) }
    }

    private fun localityOfRegionSpec(regionId: Int?) = Specification<LocalityEntity> { root, _, criteriaBuilder ->
        if (null == regionId) criteriaBuilder.conjunction()
        else criteriaBuilder.equal(root.get<LocalityEntity>("region").get<String>("id"), regionId)
    }

    private fun namePatternLocalitySpec(name: String?) = Specification<LocalityEntity> { root, _, criteriaBuilder ->
        if (null == name) criteriaBuilder.conjunction()
        else criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%${name.lowercase()}%")
    }
}
package ru.smartel.strike.service.region

import org.springframework.data.jpa.domain.Specification
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.request.reference.region.RegionCreateRequestDto
import ru.smartel.strike.dto.response.reference.region.RegionDetailDto
import ru.smartel.strike.entity.reference.RegionEntity
import ru.smartel.strike.repository.etc.CountryRepository
import ru.smartel.strike.repository.etc.RegionRepository
import ru.smartel.strike.service.Locale

@Service
@Transactional(rollbackFor = [Exception::class])
class RegionService(
    private val regionRepository: RegionRepository,
    private val countryRepository: CountryRepository,
    private val validator: RegionDtoValidator
) {
    fun list(name: String?, countryId: Int?, locale: Locale): ListWrapperDto<RegionDetailDto> {
        val regions = regionRepository.findAll(regionToCountrySpec(countryId).and(namePatternRegionSpec(name)))
        return ListWrapperDto(regions.asSequence()
            .map { RegionDetailDto(it, locale) }
            .sortedBy { it.name }
            .toList())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun create(dto: RegionCreateRequestDto): RegionDetailDto {
        validator.validateUpdateDTO(dto)
        return RegionEntity()
            .apply {
                name = dto.name!!
                country = countryRepository.getById(dto.countryId!!)
            }
            .also { regionRepository.save(it) }
            .let { RegionDetailDto(it, dto.locale!!) }
    }

    private fun regionToCountrySpec(countryId: Int?) = Specification<RegionEntity> { root, _, criteriaBuilder ->
        if (null == countryId) criteriaBuilder.conjunction()
        else criteriaBuilder.equal(root.get<Any>("country").get<Any>("id"), countryId)
    }

    private fun namePatternRegionSpec(name: String?) = Specification<RegionEntity> { root, _, criteriaBuilder ->
        if (null == name) criteriaBuilder.conjunction()
        else criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.lowercase() + "%")
    }
}
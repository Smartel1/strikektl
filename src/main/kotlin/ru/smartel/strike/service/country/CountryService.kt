package ru.smartel.strike.service.country

import org.springframework.data.jpa.domain.Specification
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.ListWrapperDto
import ru.smartel.strike.dto.request.country.CountryCreateRequestDto
import ru.smartel.strike.dto.response.reference.NamesExtendableDto
import ru.smartel.strike.dto.response.reference.country.CountryDetailDto
import ru.smartel.strike.entity.reference.CountryEntity
import ru.smartel.strike.repository.etc.CountryRepository
import ru.smartel.strike.service.Locale

@Service
@Transactional(rollbackFor = [Exception::class])
class CountryService(
    private val countryRepository: CountryRepository,
    private val validator: CountryDtoValidator,
) {
    fun list(name: String?, locale: Locale): ListWrapperDto<CountryDetailDto> {
        //find country using name (if name specified)
        val countries = countryRepository.findAll(namePatternCountrySpec(name))
        return ListWrapperDto(countries.asSequence()
            .map { CountryDetailDto(it, locale) }
            .sortedWith(comparatorByName())
            .toList())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun create(dto: CountryCreateRequestDto): CountryDetailDto {
        validator.validateUpdateDTO(dto)
        return CountryEntity()
            .apply {
               nameRu = dto.nameRu
               nameEn = dto.nameEn
               nameEs = dto.nameEs
               nameDe = dto.nameDe
            }
            .also { countryRepository.save(it) }
            .let { CountryDetailDto(it, dto.locale!!) }
    }

    private fun namePatternCountrySpec(name: String?) = Specification<CountryEntity> { root, _, criteriaBuilder ->
        if (null == name) criteriaBuilder.conjunction()
        else {
            val pattern = "%${name.lowercase()}%"
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nameRu")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nameEn")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nameEs")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nameDe")), pattern)
            )
        }
    }

    private fun comparatorByName() = Comparator<NamesExtendableDto> { o1, o2 ->
        sequenceOf(
            o1.name to o2.name,
            o1.nameRu to o2.nameRu,
            o1.nameEn to o2.nameEn,
            o1.nameEs to o2.nameEs,
            o1.nameDe to o2.nameDe
        )
            .filter { it.first != null }
            .filter { it.second != null }
            .firstOrNull()
            ?.let { it.first!!.compareTo(it.second!!) }
            ?: 0
    }
}
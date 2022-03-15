package ru.smartel.strike.service.reference

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import ru.smartel.strike.dto.response.reference.ReferenceCodeDto
import ru.smartel.strike.dto.response.reference.ReferenceNamesDto
import ru.smartel.strike.dto.response.reference.ReferenceNamesSlugDto
import ru.smartel.strike.dto.service.network.Network
import ru.smartel.strike.repository.conflict.ConflictReasonRepository
import ru.smartel.strike.repository.conflict.ConflictResultRepository
import ru.smartel.strike.repository.etc.CountryRepository
import ru.smartel.strike.repository.etc.IndustryRepository
import ru.smartel.strike.repository.etc.VideoTypeRepository
import ru.smartel.strike.repository.event.EventStatusRepository
import ru.smartel.strike.repository.event.EventTypeRepository
import ru.smartel.strike.service.Locale

@Service
class ReferenceService(
    private val conflictReasonRepository: ConflictReasonRepository,
    private val conflictResultRepository: ConflictResultRepository,
    private val eventStatusRepository: EventStatusRepository,
    private val eventTypeRepository: EventTypeRepository,
    private val industryRepository: IndustryRepository,
    private val countryRepository: CountryRepository,
    private val videoTypeRepository: VideoTypeRepository,
) {
    fun getAllReferences(locale: Locale): Map<String, List<Any>> {
        val result = HashMap<String, List<Any>>()
        //references with names
        result["conflictReasons"] = conflictReasonRepository.findAll().map { ReferenceNamesDto(it, locale) }
        result["conflictResults"] = conflictResultRepository.findAll().map { ReferenceNamesDto(it, locale) }
        result["eventTypes"] = eventTypeRepository.findAll().map { ReferenceNamesDto(it, locale) }
        result["industries"] = industryRepository.findAll().map { ReferenceNamesDto(it, locale) }
        result["countries"] = countryRepository.findAll().map { ReferenceNamesDto(it, locale) }
        //references with names and slug
        result["eventStatuses"] = eventStatusRepository.findAll().map { ReferenceNamesSlugDto(it, locale) }
        //references with code
        result["videoTypes"] = videoTypeRepository.findAll().map { ReferenceCodeDto(it) }
        result["networks"] = Network.values().map { ReferenceCodeDto(it.id, it.slug) }
        return result
    }

    fun getChecksum(): String {
        return sequenceOf(conflictReasonRepository,
            conflictResultRepository,
            eventStatusRepository,
            eventTypeRepository,
            industryRepository,
            countryRepository,
            videoTypeRepository)
            .map {
                it.findAll(Sort.by(Sort.Direction.ASC, "id"))
                    .map { entity -> entity.publicHash() }
                    .reduce { acc, entityHash -> acc * entityHash }
            }
            .toList()
            .hashCode()
            .toString()
    }
}
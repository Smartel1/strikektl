package ru.smartel.strike.repository.conflict

import org.springframework.stereotype.Repository
import ru.smartel.strike.dto.response.conflict.*
import java.time.LocalDate

@Repository
interface ReportsConflictRepository {
    /**
     * Conflicts count, that have events in specified period, but started before 'from' date
     */
    fun getOldConflictsCount(from: LocalDate, to: LocalDate, countriesIds: List<Long>): Long

    fun getCountByCountries(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByCountry>

    fun getCountByDistricts(from: LocalDate, to: LocalDate, countriesIds: List<Long>): Map<String, Int>

    fun getCountByRegions(from: LocalDate, to: LocalDate, countriesIds: List<Long>): Map<String, Int>

    fun getSpecificCountByDistricts(from: LocalDate, to: LocalDate, countriesIds: List<Long>): Map<String, Float>

    fun getCountByIndustries(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByIndustry>

    fun getCountByReasons(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByReason>

    fun getCountByResults(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByResult>

    fun getCountByTypes(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByType>

    fun getCountByResultsByTypes(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByResultsByType>

    fun getCountByResultsByIndustries(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
    ): List<CountByResultsByIndustry>

    fun getCountByReasonsByIndustries(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
    ): List<CountByReasonsByIndustry>

    fun getCountPercentByTypesByIndustries(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
    ): List<CountByTypesByIndustry>
}
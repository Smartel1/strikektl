package ru.smartel.strike.dto.response.conflict

import java.util.*
import kotlin.collections.ArrayList

data class ConflictReportDto(
    val conflictsBeganBeforeDateFromCount: Long,
    val countByCountries: List<CountByCountry> = ArrayList(),
    val countByDistricts: Map<String, Int> = HashMap(),
    val countByRegions: Map<String, Int> = HashMap(),
    val specificCountByDistricts: Map<String, Float> = HashMap(), // per citizen
    val countByIndustries: List<CountByIndustry> = ArrayList(),
    val countByReasons: List<CountByReason> = ArrayList(),
    val countByResults: List<CountByResult> = ArrayList(),
    val countByTypes: List<CountByType> = ArrayList(),
    val countByResultsByTypes: List<CountByResultsByType> = ArrayList(),
    val countByResultsByIndustries: List<CountByResultsByIndustry> = ArrayList(),
    val countByReasonsByIndustries: List<CountByReasonsByIndustry> = ArrayList(),
    val countByTypesByIndustries: List<CountByTypesByIndustry> = ArrayList()
)
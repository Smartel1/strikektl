package ru.smartel.strike.repository.conflict

import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.response.conflict.*
import java.math.BigInteger
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

const val NOT_SPECIFIED = "Не указано"

@Transactional(rollbackFor = [Exception::class])
class ReportsConflictRepositoryImpl(
    @PersistenceContext
    private val entityManager: EntityManager,
) : ReportsConflictRepository {
    override fun getOldConflictsCount(from: LocalDate, to: LocalDate, countriesIds: List<Long>): Long {
        return (entityManager.createNativeQuery(
            "select count(distinct(c.id))" +
                    " from conflicts c" +
                    " left join events e on e.conflict_id = c.id" +
                    " left join localities l on e.locality_id = l.id" +
                    " left join regions r on l.region_id = r.id" +
                    " where e.date >= :from and e.date <= :to" +
                    " and r.country_id in :countriesIds" +
                    " and c.date_from < :from")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .singleResult as BigInteger).toLong()
    }

    override fun getCountByCountries(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByCountry> {
        // conflict's country is the country of the first conflict's event (first by date)
        val resultList = entityManager.createNativeQuery(
            "with sub as (" +
                    "    select e.conflict_id," +
                    "           e.locality_id," +
                    "           ROW_NUMBER() OVER (PARTITION BY e.conflict_id ORDER BY e.date) AS rk" +
                    "    from events e where e.date >= :from and e.date <= :to)" +
                    " select r.country_id, count(conflict_id)" +
                    " from sub" +
                    "         left join localities l on l.id = sub.locality_id" +
                    "         left join regions r on r.id = l.region_id" +
                    " where rk = 1" +
                    " and r.country_id in :countriesIds" +
                    " group by r.country_id")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>
        return resultList
            .map { r ->
                CountByCountry(
                    countryId = mapNullableId(r[0]),
                    count = (r[1] as BigInteger).toLong())
            }
    }

    override fun getCountByDistricts(from: LocalDate, to: LocalDate, countriesIds: List<Long>): Map<String, Int> {
        // conflict's district is the district of the first conflict's event (first by date)
        val resultList = entityManager.createNativeQuery(
            "with sub as (" +
                    "    select e.conflict_id," +
                    "           e.locality_id," +
                    "           ROW_NUMBER() OVER (PARTITION BY e.conflict_id ORDER BY e.date) AS rk" +
                    "    from events e where e.date >= :from and e.date <= :to)" +
                    " select d.name, count(conflict_id)" +
                    " from sub" +
                    "         left join localities l on l.id = sub.locality_id" +
                    "         left join regions r on r.id = l.region_id" +
                    "         left join districts d on d.id = r.district_id" +
                    " where rk = 1" +
                    " and r.country_id in :countriesIds" +
                    " group by d.name" +
                    " having d.name is not null")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>
        return resultList
            .map { mapKey(it[0]) to (it[1] as BigInteger).toInt() }
            .toMap()
    }

    override fun getCountByRegions(from: LocalDate, to: LocalDate, countriesIds: List<Long>): Map<String, Int> {
        // Conflict's region is the region of the first conflict's event (first by date).
        // Small part of conflicts include events from several regions, so this approximation is good enough
        val resultList = entityManager.createNativeQuery(
            "with sub as (" +
                    "    select e.conflict_id," +
                    "           e.locality_id," +
                    "           ROW_NUMBER() OVER (PARTITION BY e.conflict_id ORDER BY e.date) AS rk" +
                    "    from events e where e.date >= :from and e.date <= :to)" +
                    " select r.name, count(conflict_id)" +
                    " from sub" +
                    "         left join localities l on l.id = sub.locality_id" +
                    "         left join regions r on r.id = l.region_id" +
                    " where rk = 1" +
                    " and r.country_id in :countriesIds" +
                    " group by r.name" +
                    " having r.name is not null")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>
        return resultList
            .map { mapKey(it[0]) to (it[1] as BigInteger).toInt() }
            .toMap()
    }

    override fun getSpecificCountByDistricts(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
    ): Map<String, Float> {
        // conflict's district is the district of the first conflict's event (first by date)
        val resultList = entityManager.createNativeQuery(
            "with sub as (" +
                    "    select e.conflict_id," +
                    "           e.locality_id," +
                    "           ROW_NUMBER() OVER (PARTITION BY e.conflict_id ORDER BY e.date) AS rk" +
                    "    from events e where e.date >= :from and e.date <= :to)" +
                    " select d.name, " +
                    "        cast (count(conflict_id) * 1000000 as float) / cast ((select population from districts where districts.name = d.name limit 1) as float)" +
                    " from sub" +
                    "         left join localities l on l.id = sub.locality_id" +
                    "         left join regions r on r.id = l.region_id" +
                    "         left join districts d on d.id = r.district_id" +
                    " where rk = 1" +
                    " and r.country_id in :countriesIds" +
                    " group by d.name" +
                    " having d.name is not null")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>
        return resultList
            .map { mapKey(it[0]) to (it[1] as Double).toFloat() }
            .toMap()
    }

    override fun getCountByIndustries(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByIndustry> {
        val resultList = entityManager.createNativeQuery(
            "select c.industry_id, count(distinct(c.id)) " +
                    "from conflicts c " +
                    "left join events e on e.conflict_id = c.id " +
                    "left join localities l on e.locality_id = l.id " +
                    "left join regions r on l.region_id = r.id " +
                    "where e.date >= :from and e.date <= :to " +
                    "and r.country_id in :countriesIds " +
                    "group by c.industry_id")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>
        return resultList
            .map {
                CountByIndustry(
                    industryId = mapNullableId(it[0]),
                    count = (it[1] as BigInteger).toLong())
            }
    }

    override fun getCountByReasons(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByReason> {
        val resultList = entityManager.createNativeQuery(
            "select c.conflict_reason_id, count(distinct(c.id)) " +
                    "from conflicts c " +
                    "left join events e on e.conflict_id = c.id " +
                    "left join localities l on e.locality_id = l.id " +
                    "left join regions r on l.region_id = r.id " +
                    "where e.date >= :from and e.date <= :to " +
                    "and r.country_id in :countriesIds " +
                    "group by c.conflict_reason_id")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>
        return resultList
            .map {
                CountByReason(
                    reasonId = mapNullableId(it[0]),
                    count = (it[1] as BigInteger).toLong()
                )
            }
    }

    override fun getCountByResults(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByResult> {
        val resultList = entityManager.createNativeQuery(
            "select c.conflict_result_id, count(distinct(c.id)) " +
                    "from conflicts c " +
                    "left join events e on e.conflict_id = c.id " +
                    "left join localities l on e.locality_id = l.id " +
                    "left join regions r on l.region_id = r.id " +
                    "where e.date >= :from and e.date <= :to " +
                    "and r.country_id in :countriesIds " +
                    "group by c.conflict_result_id")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>
        return resultList
            .map {
                CountByResult(mapNullableId(
                    it[0]), (it[1] as BigInteger).toLong())
            }
    }

    override fun getCountByTypes(from: LocalDate, to: LocalDate, countriesIds: List<Long>): List<CountByType> {
        val resultList = entityManager.createNativeQuery(
            "select c.main_type_id, count(distinct(c.id)) " +
                    "from conflicts c " +
                    "left join events e on e.conflict_id = c.id " +
                    "left join localities l on e.locality_id = l.id " +
                    "left join regions r on l.region_id = r.id " +
                    "where e.date >= :from and e.date <= :to " +
                    "and r.country_id in :countriesIds " +
                    "group by c.main_type_id")
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>
        return resultList
            .map {
                CountByType(
                    typeId = mapNullableId(it[0]),
                    count = (it[1] as BigInteger).toLong())
            }
    }

    override fun getCountByResultsByTypes(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
    ): List<CountByResultsByType> {
        val types = entityManager.createNativeQuery(
            "select id from event_types")
            .resultList as List<Int>
        return types
            .map { mainTypeId ->
                CountByResultsByType(
                    typeId = mainTypeId.toLong(),
                    countByResult = getCountByResult(from, to, countriesIds, mainTypeId.toLong()))
            }
    }

    private fun getCountByResult(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
        mainTypeId: Long,
    ): List<CountByResult> {
        val countToResult = (entityManager.createNativeQuery(
            "select cr.id, sub.count " +
                    "from conflict_results cr " +
                    "full outer join (select c.conflict_result_id res_id, count(distinct(c.id))" +
                    "  from conflicts c" +
                    "  left join events e on e.conflict_id = c.id" +
                    "  left join localities l on e.locality_id = l.id" +
                    "  left join regions r on l.region_id = r.id" +
                    "  where e.date >= :from and e.date <= :to" +
                    "  and r.country_id in :countriesIds" +
                    "  and c.main_type_id = :mainTypeId" +
                    "  group by c.conflict_result_id) sub on sub.res_id = cr.id")
            .setParameter("mainTypeId", mainTypeId)
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>)
            .map { raw ->
                CountByResult(
                    resultId = (raw[0] as Int?)?.toLong(),
                    count = raw[1]?.let { (it as BigInteger).toLong() } ?: 0
                )
            } // count of conflicts
            .toMutableList()
        // to always return not specified count
        if (countToResult.none { it.resultId == null }) {
            countToResult.add(CountByResult(null, 0))
        }
        return countToResult
    }

    override fun getCountByResultsByIndustries(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
    ): List<CountByResultsByIndustry> {
        val industryIds = entityManager.createNativeQuery(
            "select id from industries")
            .resultList as List<Int>
        return industryIds
            .map { industryId ->
                CountByResultsByIndustry(
                    industryId = industryId.toLong(),
                    countByResult = getCountByResultByIndustry(from, to, countriesIds, industryId.toLong())
                )
            }
    }

    private fun getCountByResultByIndustry(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
        industryId: Long,
    ): List<CountByResult> {
        val countToResult = (entityManager.createNativeQuery(
            "select cr.id, sub.count " +
                    "from conflict_results cr " +
                    "full outer join (select c.conflict_result_id res_id, count(distinct(c.id))" +
                    "  from conflicts c" +
                    "  left join events e on e.conflict_id = c.id" +
                    "  left join localities l on e.locality_id = l.id" +
                    "  left join regions r on l.region_id = r.id" +
                    "  where e.date >= :from and e.date <= :to" +
                    "  and r.country_id in :countriesIds" +
                    "  and c.industry_id = :industryId" +
                    "  group by c.conflict_result_id) sub on sub.res_id = cr.id")
            .setParameter("industryId", industryId)
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>)
            .map { raw ->
                CountByResult(
                    resultId = (raw[0] as Int?)?.toLong(),
                    count = raw[1]?.let { (it as BigInteger).toLong() } ?: 0
                )
            } // count of conflicts)
            .toMutableList()
        // to always return not specified count
        if (countToResult.stream().noneMatch { it.resultId == null }) {
            countToResult.add(CountByResult(null, 0))
        }
        return countToResult
    }

    override fun getCountByReasonsByIndustries(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
    ): List<CountByReasonsByIndustry> {
        val industryIds = entityManager.createNativeQuery(
            "select id from industries")
            .resultList as List<Int>
        return industryIds
            .map { industryId ->
                CountByReasonsByIndustry(
                    industryId = industryId.toLong(),
                    countByReason = getCountByReasonByIndustry(from, to, countriesIds, industryId.toLong())
                )
            }
    }

    private fun getCountByReasonByIndustry(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
        industryId: Long,
    ): List<CountByReason> {
        val countToReason = (entityManager.createNativeQuery(
            "select cr.id, sub.count " +
                    "from conflict_reasons cr " +
                    "full outer join (select c.conflict_reason_id res_id, count(distinct(c.id))" +
                    "  from conflicts c" +
                    "  left join events e on e.conflict_id = c.id" +
                    "  left join localities l on e.locality_id = l.id" +
                    "  left join regions r on l.region_id = r.id" +
                    "  where e.date >= :from and e.date <= :to" +
                    "  and r.country_id in :countriesIds" +
                    "  and c.industry_id = :industryId" +
                    "  group by c.conflict_reason_id) sub on sub.res_id = cr.id")
            .setParameter("industryId", industryId)
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>)
            .map { raw ->
                CountByReason(
                    reasonId = (raw[0] as Int?)?.toLong(),
                    count = raw[1]?.let { (it as BigInteger).toLong() } ?: 0
                )
            } // count of conflicts)
            .toMutableList()
        // to always return not specified count
        if (countToReason.none { it.reasonId == null }) {
            countToReason.add(CountByReason(null, 0))
        }
        return countToReason
    }

    override fun getCountPercentByTypesByIndustries(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
    ): List<CountByTypesByIndustry> {
        val industryIds = entityManager.createNativeQuery(
            "select id from industries")
            .resultList as List<Int>
        return industryIds
            .map { industryId ->
                CountByTypesByIndustry(
                    industryId = industryId.toLong(),
                    countByType = getCountByTypeByIndustry(from, to, countriesIds, industryId.toLong())
                )
            }
    }

    private fun getCountByTypeByIndustry(
        from: LocalDate,
        to: LocalDate,
        countriesIds: List<Long>,
        industryId: Long,
    ): List<CountByType> {
        val countToType = (entityManager.createNativeQuery(
            "select et.id, sub.count " +
                    "from event_types et " +
                    "full outer join (select c.main_type_id mtype_id, count(distinct(c.id))" +
                    "  from conflicts c" +
                    "  left join events e on e.conflict_id = c.id" +
                    "  left join localities l on e.locality_id = l.id" +
                    "  left join regions r on l.region_id = r.id" +
                    "  where e.date >= :from and e.date <= :to" +
                    "  and r.country_id in :countriesIds" +
                    "  and c.industry_id = :industryId" +
                    "  group by c.main_type_id) sub on sub.mtype_id = et.id")
            .setParameter("industryId", industryId)
            .setParameter("from", from)
            .setParameter("to", to)
            .setParameter("countriesIds", countriesIds)
            .resultList as List<Array<Any?>>)
            .map { raw ->
                CountByType(
                    typeId = (raw[0] as Int?)?.toLong(),
                    count = raw[1]?.let { (it as BigInteger).toLong() } ?: 0
                )
            }.toMutableList() // count of conflicts)
        // to always return not specified count
        if (countToType.none { it.typeId == null }) {
            countToType.add(CountByType(null, 0))
        }
        return countToType
    }

    private fun mapKey(key: Any?): String {
        return (key ?: NOT_SPECIFIED) as String
    }

    private fun mapNullableId(key: Any?): Long? {
        return key?.let { it as Int }?.toLong()
    }

}
package ru.smartel.strike.repository.event

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.smartel.strike.entity.EventEntity

interface EventRepository : JpaSpecificationExecutor<EventEntity>,
    JpaRepository<EventEntity, Long> {
    @EntityGraph(attributePaths = ["videos", "photos", "tags", "conflict"])
    override fun findAllById(ids: Iterable<Long?>): List<EventEntity>

    fun findAllByConflictId(conflictId: Long): List<EventEntity>

    fun countByConflictId(conflictId: Long): Long

    fun findFirstByConflictIdOrderByPostDateDesc(conflictId: Long): EventEntity?

    fun findFirstByConflictIdAndLocalityNotNullOrderByPostDateDesc(conflictId: Long): EventEntity?
}
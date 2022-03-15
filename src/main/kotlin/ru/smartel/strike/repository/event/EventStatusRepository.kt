package ru.smartel.strike.repository.event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smartel.strike.entity.reference.ConflictReasonEntity
import ru.smartel.strike.entity.reference.EventStatusEntity

@Repository
interface EventStatusRepository: JpaRepository<EventStatusEntity, Long>{
    fun findFirstBySlug(slug: String): EventStatusEntity
}
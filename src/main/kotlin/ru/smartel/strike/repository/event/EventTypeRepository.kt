package ru.smartel.strike.repository.event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smartel.strike.entity.reference.EventTypeEntity

@Repository
interface EventTypeRepository: JpaRepository<EventTypeEntity, Long>

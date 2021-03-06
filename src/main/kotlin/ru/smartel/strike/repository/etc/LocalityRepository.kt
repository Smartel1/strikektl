package ru.smartel.strike.repository.etc

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import ru.smartel.strike.entity.reference.LocalityEntity

@Repository
interface LocalityRepository: JpaRepository<LocalityEntity, Long>, JpaSpecificationExecutor<LocalityEntity>
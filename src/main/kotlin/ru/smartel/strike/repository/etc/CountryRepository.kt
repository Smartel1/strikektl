package ru.smartel.strike.repository.etc

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import ru.smartel.strike.entity.reference.CountryEntity

@Repository
interface CountryRepository: JpaRepository<CountryEntity, Long>, JpaSpecificationExecutor<CountryEntity>
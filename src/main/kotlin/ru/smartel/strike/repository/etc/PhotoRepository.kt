package ru.smartel.strike.repository.etc

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smartel.strike.entity.PhotoEntity

@Repository
interface PhotoRepository: JpaRepository<PhotoEntity, Long>
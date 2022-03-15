package ru.smartel.strike.repository.etc

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smartel.strike.entity.TagEntity
import java.util.*

@Repository
interface TagRepository: JpaRepository<TagEntity, Long> {
    fun findFirstByName(name: String) : Optional<TagEntity>
}
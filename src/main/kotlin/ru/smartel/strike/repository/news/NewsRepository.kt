package ru.smartel.strike.repository.news

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import ru.smartel.strike.entity.NewsEntity

interface NewsRepository : JpaSpecificationExecutor<NewsEntity>,
    JpaRepository<NewsEntity, Long> {
    @EntityGraph(attributePaths = ["videos", "photos", "tags"])
    override fun findAllById(ids: Iterable<Long?>): List<NewsEntity>
}
package ru.smartel.strike.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.exsio.nestedj.DelegatingNestedNodeRepository
import pl.exsio.nestedj.NestedNodeRepository
import pl.exsio.nestedj.config.jpa.JpaNestedNodeRepositoryConfiguration
import pl.exsio.nestedj.delegate.control.*
import pl.exsio.nestedj.delegate.query.jpa.*
import ru.smartel.strike.entity.ConflictEntity
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Configuration
class NestedTreeConfiguration(@PersistenceContext private val entityManager: EntityManager) {
    @Bean
    fun conflictNestedNodeRepository(): NestedNodeRepository<Long, ConflictEntity>? {
        val configuration = JpaNestedNodeRepositoryConfiguration(
            entityManager, ConflictEntity::class.java, Long::class.java
        )

        // Temporary crutch
        val inserter = QueryBasedNestedNodeInserter(JpaNestedNodeInsertingQueryDelegate(configuration))
        val retriever = QueryBasedNestedNodeRetriever(JpaNestedNodeRetrievingQueryDelegate(configuration))
        return DelegatingNestedNodeRepository(
            QueryBasedNestedNodeMover(JpaNestedNodeMovingQueryDelegate(configuration)),
            QueryBasedNestedNodeRemover(JpaNestedNodeIRemovingQueryDelegate(configuration)),
            retriever,
            QueryBasedNestedNodeRebuilder(inserter, retriever, JpaNestedNodeRebuildingQueryDelegate(configuration)),
            inserter
        )
    }
}
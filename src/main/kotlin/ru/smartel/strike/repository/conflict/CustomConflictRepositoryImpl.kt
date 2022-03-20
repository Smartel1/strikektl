package ru.smartel.strike.repository.conflict

import org.springframework.data.jpa.domain.Specification
import org.springframework.transaction.annotation.Transactional
import pl.exsio.nestedj.NestedNodeRepository
import ru.smartel.strike.dto.service.sort.ConflictSortDto
import ru.smartel.strike.entity.ConflictEntity
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.Root

@Transactional(rollbackFor = [Exception::class])
class CustomConflictRepositoryImpl(
    @PersistenceContext
    private val entityManager: EntityManager,
    private val conflictNestedNodeRepository: NestedNodeRepository<Long, ConflictEntity>,
) : CustomConflictRepository {

    override fun findAllByIdGetParentEventId(ids: List<Long>): List<Long> {
        return entityManager.createQuery("select parentEvent.id from ConflictEntity where id in :ids")
            .setParameter("ids", ids)
            .resultList as List<Long>
    }

    override fun findIds(
        specification: Specification<ConflictEntity>,
        sortDTO: ConflictSortDto,
        page: Int,
        perPage: Int,
    ): List<Long> {
        val cb = entityManager.criteriaBuilder
        val idQuery = cb.createQuery(Long::class.java)
        val root: Root<ConflictEntity> = idQuery.from(ConflictEntity::class.java)
        idQuery.select(root.get("id"))
        idQuery.orderBy(sortDTO.toOrder(cb, root))
        idQuery.where(specification.toPredicate(root, idQuery, cb))

        return entityManager.createQuery(idQuery)
            .setMaxResults(perPage)
            .setFirstResult((page - 1) * perPage)
            .resultList
    }

    override fun insertAsLastChildOf(conflict: ConflictEntity, parent: ConflictEntity?) {
        if (parent == null) {
            if (conflict.parentId != null || conflict.treeRight == null) {
                // set service fields if not set or if set incorrect
                conflictNestedNodeRepository.insertAsLastRoot(conflict)
            }
        } else {
            if (parent.getId() != conflict.parentId || conflict.treeRight == null) {
                // set service fields if not set or if set incorrect
                conflictNestedNodeRepository.insertAsLastChildOf(conflict, parent)
            }
        }
        entityManager.persist(conflict)
    }

    override fun hasChildren(conflict: ConflictEntity): Boolean {
        val childrenCount = entityManager.createQuery("select count(c) from ConflictEntity c where c.parent = :id")
            .setParameter("id", conflict.getId())
            .singleResult as Long
        return childrenCount != 0L
    }

    override fun getRootConflict(conflict: ConflictEntity): ConflictEntity {
        return entityManager.createQuery(
            "select c from ConflictEntity c where 'level' = :rootLevel and 'left' <= :lft and 'right' >= :rgt")
            .setParameter("rootLevel", 0L)
            .setParameter("rgt", conflict.treeRight)
            .setParameter("lft", conflict.treeLeft)
            .singleResult as ConflictEntity
    }

    override fun getDescendantsAndSelf(conflict: ConflictEntity): List<ConflictEntity> {
        return entityManager.createQuery(
            "select c from ConflictEntity c where 'left' >= :lft and 'right' <= :rgt")
            .setParameter("rgt", conflict.treeRight)
            .setParameter("lft", conflict.treeLeft)
            .resultList as List<ConflictEntity>
    }

    override fun deleteFromTree(conflict: ConflictEntity) {
        conflictNestedNodeRepository.removeSingle(conflict)
    }
}
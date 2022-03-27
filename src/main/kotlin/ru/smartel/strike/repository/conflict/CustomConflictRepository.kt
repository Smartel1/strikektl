package ru.smartel.strike.repository.conflict

import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import ru.smartel.strike.dto.service.sort.conflict.ConflictSortDto
import ru.smartel.strike.entity.ConflictEntity

@Repository
interface CustomConflictRepository {
    fun findAllByIdGetParentEventId(ids: List<Long>): List<Long>

    fun findIds(
        specification: Specification<ConflictEntity>,
        sortDTO: ConflictSortDto,
        page: Int,
        perPage: Int,
    ): List<Long>

    /**
     * Persist conflict as child of another conflict or as root (if parent == null).
     * In case of conflict already exists in tree - replace
     *
     * @param conflict transient or persistent conflict
     * @param parent   persistent parent conflict or null
     */
    fun insertAsLastChildOf(conflict: ConflictEntity, parent: ConflictEntity?)

    /**
     * Return true if conflict has children
     *
     * @param conflict conflict to check
     * @return true if conflict has child conflicts
     */
    fun hasChildren(conflict: ConflictEntity): Boolean

    /**
     * Get root conflict of conflict tree
     *
     * @param conflict
     * @return
     */
    fun getRootConflict(conflict: ConflictEntity): ConflictEntity

    /**
     * Get conflict's descendants (not only children)
     *
     * @param conflict
     * @return
     */
    fun getDescendantsAndSelf(conflict: ConflictEntity): List<ConflictEntity>

    /**
     * Remove conflict from tree
     *
     * @param conflict conflict to remove
     */
    fun deleteFromTree(conflict: ConflictEntity)
}
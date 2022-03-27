package ru.smartel.strike.dto.service.sort.conflict

import ru.smartel.strike.dto.request.Sort
import ru.smartel.strike.entity.ConflictEntity
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root

class ConflictSortDto private constructor(
    private val orderField: OrderField,
    private val orderDirection: OrderDirection,
) {
    companion object {
        fun of(sort: Sort?): ConflictSortDto {
            if (sort == null) {
                return ConflictSortDto(OrderField.CREATED_AT, OrderDirection.DESC)
            }
            val orderField = if (sort.field == "createdAt") OrderField.CREATED_AT
            else throw IllegalStateException("Unknown sorting field for conflict: ${sort.field}")

            val orderDirection = when (sort.order) {
                "asc" -> OrderDirection.ASC
                "desc" -> OrderDirection.DESC
                else -> throw IllegalStateException("Unknown sorting order for conflict: ${sort.order}")
            }
            return ConflictSortDto(orderField, orderDirection)
        }
    }

    fun toOrder(cb: CriteriaBuilder, root: Root<ConflictEntity>) =
        orderDirection.getOrder(cb, orderField.getPath(root))

    fun toComparator(): Comparator<ConflictEntity> =
        if (orderDirection == OrderDirection.ASC) orderField.comparator
        else orderField.comparator.reversed()
}

enum class OrderField(
    val pathMaker: (Root<ConflictEntity>) -> Path<ConflictEntity>,
    val comparator: Comparator<ConflictEntity>,
) {
    CREATED_AT({ root: Root<ConflictEntity> -> root.get<ConflictEntity>("createdAt") },
        java.util.Comparator<ConflictEntity> { c1, c2 -> c1.createdAt!!.compareTo(c2.createdAt) });

    fun getPath(root: Root<ConflictEntity>) = pathMaker(root)
}

enum class OrderDirection(
    private val orderMaker: (CriteriaBuilder, Path<ConflictEntity>) -> Order,
) {
    ASC(CriteriaBuilder::asc),
    DESC(CriteriaBuilder::desc);

    fun getOrder(cb: CriteriaBuilder, path: Path<ConflictEntity>) = orderMaker(cb, path)
}
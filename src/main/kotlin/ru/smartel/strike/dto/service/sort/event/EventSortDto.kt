package ru.smartel.strike.dto.service.sort.event

import ru.smartel.strike.dto.request.Sort
import ru.smartel.strike.entity.EventEntity
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Order
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root

class EventSortDto private constructor(
    private val orderField: OrderField,
    private val orderDirection: OrderDirection,
) {
    companion object {
        fun of(sort: Sort?): EventSortDto {
            if (sort == null) {
                return EventSortDto(OrderField.DATE, OrderDirection.DESC)
            }
            val orderField = when (sort.field ) {
                "createdAt" -> OrderField.CREATED_AT
                "date" -> OrderField.DATE
                "views" -> OrderField.VIEWS
                else -> throw IllegalStateException("Unknown sorting field for event: ${sort.field}")
            }
            val orderDirection = when (sort.order) {
                "asc" -> OrderDirection.ASC
                "desc" -> OrderDirection.DESC
                else -> throw IllegalStateException("Unknown sorting order for event: ${sort.order}")
            }
            return EventSortDto(orderField, orderDirection)
        }
    }

    fun toOrder(cb: CriteriaBuilder, root: Root<EventEntity>) =
        orderDirection.getOrder(cb, orderField.getPath(root))

    fun toComparator(): Comparator<EventEntity> =
        if (orderDirection == OrderDirection.ASC) orderField.comparator
        else orderField.comparator.reversed()
}

enum class OrderField(
    val pathMaker: (Root<EventEntity>) -> Path<EventEntity>,
    val comparator: Comparator<EventEntity>,
) {
    CREATED_AT({ root: Root<EventEntity> -> root.get<EventEntity>("createdAt") },
        java.util.Comparator<EventEntity> { c1, c2 -> c1.createdAt!!.compareTo(c2.createdAt) }),
    DATE({ root: Root<EventEntity> -> root.get<EventEntity>("post").get<EventEntity>("date") },
        java.util.Comparator<EventEntity> { c1, c2 -> c1.post.date.compareTo(c2.post.date) }),
    VIEWS({ root: Root<EventEntity> -> root.get<EventEntity>("post").get<EventEntity>("views") },
        java.util.Comparator<EventEntity> { c1, c2 -> c1.post.views.compareTo(c2.post.views) });

    fun getPath(root: Root<EventEntity>) = pathMaker(root)
}

enum class OrderDirection(
    private val orderMaker: (CriteriaBuilder, Path<EventEntity>) -> Order,
) {
    ASC(CriteriaBuilder::asc),
    DESC(CriteriaBuilder::desc);

    fun getOrder(cb: CriteriaBuilder, path: Path<EventEntity>) = orderMaker(cb, path)
}
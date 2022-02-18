package ru.smartel.strike.entity.reference

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "event_statuses")
class EventStatusEntity: EntityWithNamesAndSlug() {
    companion object {
        const val new = "new"
        const val intermediate = "intermediate"
        const val final = "final"
    }
}
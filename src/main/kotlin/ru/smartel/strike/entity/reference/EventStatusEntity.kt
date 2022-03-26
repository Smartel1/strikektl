package ru.smartel.strike.entity.reference

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "event_statuses")
class EventStatusEntity: EntityWithNamesAndSlug() {
    companion object {
        const val NEW = "new"
        const val INTERMEDIATE = "intermediate"
        const val FINAL = "final"
    }
}
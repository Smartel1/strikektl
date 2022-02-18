package ru.smartel.strike.entity.reference

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "event_types")
class EventTypeEntity : EntityWithNames() {
    @Column(name = "priority")
    var priority: Int = 0
}
package ru.smartel.strike.entity.reference

import ru.smartel.strike.entity.`interface`.HavingSlug
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class EntityWithNamesAndSlug : EntityWithNames(), HavingSlug {
    @Column(name = "slug")
    override lateinit var slug: String
}

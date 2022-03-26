package ru.smartel.strike.entity.reference

import ru.smartel.strike.entity.interfaces.HavingSlug
import ru.smartel.strike.entity.interfaces.PublicReference
import java.util.*
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class EntityWithNamesAndSlug : EntityWithNames(), HavingSlug, PublicReference {
    @Column(name = "slug")
    override lateinit var slug: String

    override fun publicHash() = Objects.hash(id, nameRu, nameEn, nameEs, nameDe, slug)
}

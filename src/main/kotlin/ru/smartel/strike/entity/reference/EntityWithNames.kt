package ru.smartel.strike.entity.reference

import org.springframework.data.annotation.AccessType
import ru.smartel.strike.entity.interfaces.HavingNames
import ru.smartel.strike.entity.interfaces.PublicReference
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class EntityWithNames : HavingNames, PublicReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @AccessType(AccessType.Type.PROPERTY) // in order to get id without loading whole entity from db
    var id: Long = 0

    @Column(name = "name_ru")
    override var nameRu: String? = null

    @Column(name = "name_en")
    override var nameEn: String? = null

    @Column(name = "name_es")
    override var nameEs: String? = null

    @Column(name = "name_de")
    override var nameDe: String? = null

    override fun publicHash() = Objects.hash(id, nameRu, nameEn, nameEs, nameDe)
}

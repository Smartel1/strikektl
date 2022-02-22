package ru.smartel.strike.entity.reference

import org.springframework.data.annotation.AccessType
import ru.smartel.strike.entity.`interface`.HavingCode
import javax.persistence.*

@MappedSuperclass
abstract class ReferenceWithCode : HavingCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @AccessType(AccessType.Type.PROPERTY) // in order to get id without loading whole entity from db
    var id: Long = 0

    @Column(name = "code")
    override lateinit var code: String
}
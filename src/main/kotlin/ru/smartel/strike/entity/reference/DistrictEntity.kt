package ru.smartel.strike.entity.reference

import javax.persistence.*

@Entity
@Table(name = "districts")
class DistrictEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "name")
    var name: String = ""

    @Column(name = "population")
    var population: Int = 0
}
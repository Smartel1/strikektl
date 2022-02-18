package ru.smartel.strike.entity.reference

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "conflict_results")
class ConflictResultEntity: EntityWithNames()
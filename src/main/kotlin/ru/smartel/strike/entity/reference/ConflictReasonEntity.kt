package ru.smartel.strike.entity.reference

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "conflict_reasons")
class ConflictReasonEntity: EntityWithNames()
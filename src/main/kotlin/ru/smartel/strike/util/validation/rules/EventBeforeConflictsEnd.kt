package ru.smartel.strike.util.validation.rules

import java.time.LocalDateTime

class EventBeforeConflictsEnd(
    private val eventDate: LocalDateTime,
    private val conflictDateTo: LocalDateTime?,
    condition: Boolean = true
) : BusinessRule(condition) {

    override fun passes() = conflictDateTo == null || conflictDateTo >= eventDate

    override fun message() = "Событие не должно произойти после завершения конфликта"
}
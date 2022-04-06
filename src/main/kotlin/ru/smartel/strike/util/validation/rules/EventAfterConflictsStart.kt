package ru.smartel.strike.util.validation.rules

import java.time.LocalDateTime

class EventAfterConflictsStart(
    private val eventDate: LocalDateTime,
    private val conflictDateFrom: LocalDateTime?,
    condition: Boolean = true
) : BusinessRule(condition) {

    override fun passes() = conflictDateFrom == null || conflictDateFrom <= eventDate

    override fun message() = "Событие не должно произойти до начала конфликта"
}
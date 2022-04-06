package ru.smartel.strike.util.validation.rules

abstract class BusinessRule(
    var condition: Boolean = true
) {
    /**
     * Returns true if rule passes
     */
    abstract fun passes(): Boolean

    /**
     * Forms error message
     */
    abstract fun message(): String
}
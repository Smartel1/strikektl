package ru.smartel.strike.util

import ru.smartel.strike.exception.ValidationException


fun HashMap<String, ArrayList<String>>.addErrorIf(field: String, errorMessage: String, condition: () -> Boolean):
        HashMap<String, ArrayList<String>> {
    if (!condition()) {
        return this
    }

    if (containsKey(field)) {
        get(field)!!.add(errorMessage)
    } else {
        val errors = ArrayList<String>()
            .also { it.add(errorMessage) }
        put(field, errors)
    }
    return this
}

fun HashMap<String, ArrayList<String>>.throwIfFail() {
    if (isEmpty()) return;
    throw ValidationException(this)
}

fun notNull() = "must not be null";
fun required() = "must be present"
fun oneOf(availableItems: Collection<*>) = "must be one of: $availableItems"
fun min(min: Int) = "must be longer/more than $min"
fun max(max: Int) = "must be shorter/less than $max"
fun numericCollection() = "must contain only numeric elements or nulls"



package ru.smartel.strike.dto.response

const val IGNORE = "_1%#_"

/**
 * For using with JsonIgnore
 */
class JsonDefaultStringFilter {
    override fun equals(other: Any?): Boolean {
        return IGNORE == other
    }
}
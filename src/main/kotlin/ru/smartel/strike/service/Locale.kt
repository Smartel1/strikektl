package ru.smartel.strike.service

enum class Locale {
    RU, EN, ES, DE, ALL;

    fun pascalCase(): String = when (this) {
        RU -> "Ru"
        EN -> "En"
        ES -> "Es"
        DE -> "De"
        ALL -> "All"
    }
}
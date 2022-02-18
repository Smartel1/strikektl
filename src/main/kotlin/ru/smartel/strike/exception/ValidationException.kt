package ru.smartel.strike.exception

class ValidationException(val errors: Map<String, List<String>>?) : RuntimeException("Validation error occurred: $errors")

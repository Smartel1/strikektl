package ru.smartel.strike.util

import org.springframework.data.jpa.domain.Specification

fun <T> emptySpecification() = Specification<T> { _, _, criteriaBuilder ->
    criteriaBuilder.and();
}

fun <T> falseSpecification() = Specification<T> { _, _, criteriaBuilder ->
    criteriaBuilder.or();
}
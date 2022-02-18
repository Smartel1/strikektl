package ru.smartel.strike.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class ListWrapperDto<T>(
    val data: List<T>,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val meta: ListWrapperDtoMeta? = null
)
package ru.smartel.strike.dto.request.client_version

import io.swagger.annotations.ApiParam

data class ClientVersionGetNewRequestDto(
    @field:ApiParam(value = "Текущая версия приложения на утройстве")
    val currentVersion: String?,
    @field:ApiParam(value = "Идентификатор клиента", example = "org.nrstudio.strikecom")
    val clientId: String?
)

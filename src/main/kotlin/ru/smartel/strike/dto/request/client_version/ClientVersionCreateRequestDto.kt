package ru.smartel.strike.dto.request.client_version

data class ClientVersionCreateRequestDto(
    val version: String?,
    val clientId: String?,
    val required: Boolean?,
    val descriptionRu: String?,
    val descriptionEn: String?,
    val descriptionEs: String?,
    val descriptionDe: String?
)
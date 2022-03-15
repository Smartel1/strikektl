package ru.smartel.strike.dto.service.network

enum class Network(
    val id: Long,
    val slug: String,
) {
    TELEGRAM(1L, "telegram"),
    OK(2L, "odnoklassniki"),
    VK(3L, "vkontakte")
}
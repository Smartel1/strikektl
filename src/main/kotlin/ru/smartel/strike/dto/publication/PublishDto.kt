package ru.smartel.strike.dto.publication

data class PublishDto(
    val text: String,
    val sourceUrl: String,
    val sitePageUrl: String,
    val videoUrls: MutableList<String>
)

package ru.smartel.strike.dto.publication

data class PublishDtoWithNetworks(
    val data: PublishDto,
    val publishTo: Set<Long>
)

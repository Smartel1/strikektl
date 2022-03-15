package ru.smartel.strike.dto.response.user

import ru.smartel.strike.entity.UserEntity

data class UserDetailDto(
    var id: Long = 0,
    val name: String?,
    val uuid: String,
    val email: String?,
    val fcm: String?,
    val roles: List<String>,
    val imageUrl: String?,
    val favouriteEvents: List<Long>,
    val favouriteNews: List<Long>?,
) {
    constructor(user: UserEntity) : this(
        user.id,
        user.name,
        user.uid,
        user.email,
        user.fcm,
        user.getRolesAsList(),
        user.imageUrl,
        user.favouriteEvents.map { it.id },
        user.favouriteNews.map { it.id }
    )
}

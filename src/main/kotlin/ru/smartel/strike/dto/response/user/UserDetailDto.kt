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
        id = user.id,
        name = user.name,
        uuid = user.uid,
        email = user.email,
        fcm = user.fcm,
        roles = user.getRolesAsList(),
        imageUrl = user.imageUrl,
        favouriteEvents = user.favouriteEvents.map { it.id },
        favouriteNews = user.favouriteNews.map { it.id }
    )
}

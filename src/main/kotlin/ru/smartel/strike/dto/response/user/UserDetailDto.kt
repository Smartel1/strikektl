package ru.smartel.strike.dto.response.user

import ru.smartel.strike.entity.UserEntity

data class UserDetailDto(
    private var id: Long = 0,
    private val name: String?,
    private val uuid: String,
    private val email: String?,
    private val fcm: String?,
    private val roles: List<String>,
    private val imageUrl: String?,
    private val favouriteEvents: List<Long>,
    private val favouriteNews: List<Long>?
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

package ru.smartel.strike.dto.response.user

import ru.smartel.strike.entity.UserEntity

data class AuthorDto(
    var id: Long = 0,
    val name: String?,
    val email: String?,
    val imageUrl: String?
) {
    constructor(user: UserEntity) : this(
        id = user.id,
        name = user.name,
        email = user.email,
        imageUrl = user.imageUrl
    )
}

package ru.smartel.strike.security.token

import io.swagger.annotations.ApiParam
import ru.smartel.strike.entity.UserEntity

data class UserPrincipal(
    @ApiParam(hidden = true)
    val id: Long,
    @ApiParam(hidden = true)
    val name: String?,
    @ApiParam(hidden = true)
    val roles: List<String>
) {
    companion object {
        fun from(user: UserEntity) = UserPrincipal(user.id, user.name, user.getRolesAsList())
    }
}

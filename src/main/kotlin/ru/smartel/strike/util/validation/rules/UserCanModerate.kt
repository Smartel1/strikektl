package ru.smartel.strike.util.validation.rules

import ru.smartel.strike.security.token.UserPrincipal

class UserCanModerate(private val user: UserPrincipal, condition: Boolean) : BusinessRule(condition) {

    override fun passes() = user.canModerate()

    override fun message() = "Пользователь должен обладать правами модератора"
}
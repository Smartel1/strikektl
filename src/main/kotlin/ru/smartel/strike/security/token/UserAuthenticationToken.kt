package ru.smartel.strike.security.token

import com.google.firebase.auth.FirebaseToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class UserAuthenticationToken(
    private val user: UserPrincipal,
    private val authorities: MutableSet<GrantedAuthority>,
    private val credentials: FirebaseToken?,
    private var authenticated: Boolean
) : Authentication {
    override fun getName() = user.name

    override fun getAuthorities() = authorities

    override fun getCredentials() = credentials

    override fun getDetails() = null

    override fun getPrincipal() = user

    override fun isAuthenticated() = authenticated

    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }
}
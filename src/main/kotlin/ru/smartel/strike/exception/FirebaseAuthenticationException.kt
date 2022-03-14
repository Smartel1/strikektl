package ru.smartel.strike.exception

import org.springframework.security.core.AuthenticationException

class FirebaseAuthenticationException(msg: String) : AuthenticationException(msg)

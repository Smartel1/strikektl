package ru.smartel.strike.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpStatus
import org.apache.http.entity.ContentType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.smartel.strike.configuration.properties.FirebaseProperties
import ru.smartel.strike.dto.exception.ApiErrorDto
import ru.smartel.strike.entity.UserEntity
import ru.smartel.strike.security.token.UserAuthenticationToken
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.firebase.FirebaseService
import ru.smartel.strike.service.user.UserService
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.CompletableFuture
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class FirebaseTokenFilter(
    private val objectMapper: ObjectMapper,
    private val firebaseProperties: FirebaseProperties,
    private val firebaseService: FirebaseService,
    private val userService: UserService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        if (firebaseProperties.authStub) {
            authenticateAsModerator()
            chain.doFilter(request, response)
            return
        }

        if (firebaseService.firebaseAuthNotSet()) {
            writeErrorToResponse(response, "Не сконфигурирован firebase", HttpStatus.SC_UNAUTHORIZED)
            return
        }

        val bearer = request.getHeader("Authorization")

        try {
            if (bearer != null) {
                logger.debug("User uses bearer: $bearer")
                if (!bearer.startsWith("bearer ", true)) {
                    throw BadCredentialsException("Некорректный заголовок Authorization (должен начинаться с 'bearer')")
                }
                authenticate(bearer.substring(7))
            }
        } catch (ex: AuthenticationException) {
            writeErrorToResponse(response, ex.localizedMessage, HttpStatus.SC_UNAUTHORIZED)
            return
        } catch (e: ConcurrentModificationException) { // multiple registrations of same user at the same moment
            writeErrorToResponse(response, e.message!!, HttpStatus.SC_CONFLICT)
            return
        }

        chain.doFilter(request, response)
    }

    private fun authenticate(bearer: String) {
        val token = firebaseService.parseAndVerifyToken(bearer)
        val uid = token.claims["sub"] as String
        val user = firebaseService.getOrRegisterUser(uid)
        val tokenIssuedAt = LocalDateTime.ofEpochSecond(
            token.claims["iat"].toString().toLong(), 0, ZoneOffset.ofHours(3)
        )

        // If token was issued after user was updated last time, then update user fields (async)
        if (user.updatedAt == null || user.updatedAt!! < tokenIssuedAt) {
            CompletableFuture.runAsync { firebaseService.syncUserFields(uid) }
        }
        SecurityContextHolder.getContext().authentication =
            UserAuthenticationToken(UserPrincipal.from(user), user.getAuthorities(), token, true)
    }

    private fun authenticateAsModerator() {
        val user = userService.get("stub").orElseThrow { RuntimeException("Expected default moderator to exist") }
        SecurityContextHolder.getContext().authentication = UserAuthenticationToken(
            UserPrincipal.from(user), user.getAuthorities(), null, true
        )
    }

    private fun UserEntity.getAuthorities(): MutableSet<GrantedAuthority> {
        return getRolesAsList().asSequence()
            .map { roleName -> SimpleGrantedAuthority("ROLE_$roleName") }
            .toMutableSet()
    }

    private fun writeErrorToResponse(response: HttpServletResponse, message: String, statusCode: Int) {
        response.status = statusCode
        response.contentType = ContentType.APPLICATION_JSON.toString()
        val errorMessageJson = objectMapper.writeValueAsString(ApiErrorDto("Проблемы при аутентификации", message))
        response.writer.write(errorMessageJson)
    }
}
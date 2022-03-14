package ru.smartel.strike.service.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import com.google.firebase.auth.UserRecord
import org.springframework.stereotype.Service
import ru.smartel.strike.entity.UserEntity
import ru.smartel.strike.exception.FirebaseAuthenticationException
import ru.smartel.strike.service.user.UserService
import java.util.*

@Service
class FirebaseService(
    private val firebaseAuth: FirebaseAuth?,
    private val userService: UserService
) {

    companion object {
        const val DEFAULT_USER_NAME = "unnamed user";
    }

    fun firebaseAuthNotSet(): Boolean {
        return Objects.isNull(firebaseAuth)
    }

    fun parseAndVerifyToken(jwt: String): FirebaseToken {
        check(!Objects.isNull(firebaseAuth)) { "FirebaseAuth not set" }
        return try {
            firebaseAuth!!.verifyIdToken(jwt)
        } catch (e: FirebaseAuthException) {
            throw FirebaseAuthenticationException(e.localizedMessage)
        }
    }

    fun getOrRegisterUser(uid: String): UserEntity {
        return userService.get(uid)
            .orElseGet {
                with(queryUserInfo(uid)) {
                    userService.register(uid, displayName ?: DEFAULT_USER_NAME, email, photoUrl)
                }
            }
    }

    fun syncUserFields(uid: String) {
        with(queryUserInfo(uid)) {
            userService.updateIfPresent(uid, displayName ?: DEFAULT_USER_NAME, email, photoUrl)
        }
    }

    private fun queryUserInfo(uid: String): UserRecord {
        check(!Objects.isNull(firebaseAuth)) { "FirebaseAuth been not set" }
        return try {
            firebaseAuth!!.getUser(uid)
        } catch (e: FirebaseAuthException) {
            throw FirebaseAuthenticationException(e.localizedMessage)
        }
    }
}
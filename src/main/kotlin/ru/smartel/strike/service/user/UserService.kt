package ru.smartel.strike.service.user

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.smartel.strike.dto.request.user.UserUpdateRequestDto
import ru.smartel.strike.dto.response.user.UserDetailDto
import ru.smartel.strike.entity.UserEntity
import ru.smartel.strike.repository.etc.UserRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.persistence.EntityNotFoundException

@Service
@Transactional(rollbackFor = [Exception::class])
class UserService(
    private val userRepository: UserRepository,
    private val validator: UserDtoValidator
) {
    companion object {
        /**
         * Key is uid, value is unused boolean
         */
        private val registrationMonitor = ConcurrentHashMap<String, Boolean>()
    }

    fun get(userId: Long) = userRepository.findById(userId)
        .orElseThrow { EntityNotFoundException("Пользователь не найден") }
        .let { UserDetailDto(it) }

    fun get(uid: String) = userRepository.findFirstByUid(uid)

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or principal.getId() == #dto.userId and null == #dto.roles")
    fun updateOrCreate(dto: UserUpdateRequestDto): UserDetailDto {
        validator.validateUpdateDTO(dto)

        val user = userRepository.findById(dto.userId!!)
            .orElseThrow { EntityNotFoundException("Пользователь не найден") }

        dto.fcm?.also { user.fcm = it }
        dto.roles?.also { user.setRoles(it) }

        return UserDetailDto(user)
    }

    /**
     * Register new user
     * This method is synchronized by uid
     */
    fun register(uid: String, name: String?, email: String?, imageUrl: String?): UserEntity {
        if (registrationMonitor.containsKey(uid)) {
            throw ConcurrentModificationException("Multiple registrations of same user!")
        }
        registrationMonitor[uid] = true
        return UserEntity(uid = uid, name = name, email = email, imageUrl = imageUrl)
            .also { userRepository.saveAndFlush(it) }
            .also { registrationMonitor.remove(uid) }
    }

    /**
     * Update user fields. If user not found then do nothing
     */
    fun updateIfPresent(uid: String, name: String?, email: String?, imageUrl: String?) {
        userRepository
            .findFirstByUid(uid)
            .map {
                it.name = name
                it.email = email
                it.imageUrl = imageUrl
            }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or principal.getId() == #userId")
    fun delete(userId: Long) {
        userRepository.deleteById(userId)
    }
}
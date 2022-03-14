package ru.smartel.strike.repository.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smartel.strike.entity.UserEntity
import java.util.*

@Repository
interface UserRepository: JpaRepository<UserEntity, Long> {
    fun findFirstByUid(uid: String): Optional<UserEntity>
}
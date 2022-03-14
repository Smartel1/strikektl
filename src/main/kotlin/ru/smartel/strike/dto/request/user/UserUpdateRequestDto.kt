package ru.smartel.strike.dto.request.user

data class UserUpdateRequestDto(
    var userId: Long?,
    /**
     * FCM registration token (firebase cloud messaging)
     */
    var fcm: String?,
    var roles: List<String>?
)

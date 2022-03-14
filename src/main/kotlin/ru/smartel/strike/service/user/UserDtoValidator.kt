package ru.smartel.strike.service.user

import org.springframework.stereotype.Component
import ru.smartel.strike.dto.request.user.UserUpdateRequestDto
import ru.smartel.strike.util.addErrorIf
import ru.smartel.strike.util.notNull
import ru.smartel.strike.util.throwIfFail

@Component
class UserDtoValidator {
    fun validateUpdateDTO(request: UserUpdateRequestDto) {
        HashMap<String, ArrayList<String>>()
            .addErrorIf("userId", notNull()) { request.userId == null }
            .throwIfFail()
    }
}
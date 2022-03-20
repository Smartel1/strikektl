package ru.smartel.strike.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.request.conflict.ConflictListRequestDto
import ru.smartel.strike.dto.response.conflict.ConflictListDto
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.conflict.ConflictService

@RestController
@RequestMapping("/api/v2/{locale}/conflicts")
class ConflictController(
    private val conflictService: ConflictService,
) {
    @GetMapping
    fun index(
        dto: ConflictListRequestDto,
        @AuthenticationPrincipal user: UserPrincipal,
    ): ListWrapperDto<ConflictListDto> {
        dto.user = user
        return conflictService.list(dto)
    }
}
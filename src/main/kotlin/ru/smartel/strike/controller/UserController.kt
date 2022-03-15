package ru.smartel.strike.controller

import io.swagger.annotations.ApiOperation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.request.user.UserUpdateRequestDto
import ru.smartel.strike.dto.response.user.UserDetailDto
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.user.UserService

@RestController
@RequestMapping("/api/v2/{locale}/me")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    @PreAuthorize("isFullyAuthenticated()")
    fun get(@AuthenticationPrincipal user: UserPrincipal): DetailWrapperDto<UserDetailDto> {
        return DetailWrapperDto(userService.get(user.id))
    }

    @ApiOperation("Обновление информации о пользователе (не только себя, модераторы могут менять других пользователей)")
    @PutMapping
    @PreAuthorize("isFullyAuthenticated()")
    fun update(@AuthenticationPrincipal user: UserPrincipal, @RequestBody dto: UserUpdateRequestDto):
            DetailWrapperDto<UserDetailDto> {
        dto.userId = user.id
        return DetailWrapperDto(userService.updateOrCreate(dto));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isFullyAuthenticated()")
    fun delete(@AuthenticationPrincipal user: UserPrincipal) {
        userService.delete(user.id)
    }
}
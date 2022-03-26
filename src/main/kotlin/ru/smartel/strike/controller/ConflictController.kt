package ru.smartel.strike.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.request.conflict.ConflictListRequestDto
import ru.smartel.strike.dto.request.conflict.FavouriteRequestDto
import ru.smartel.strike.dto.response.conflict.ConflictDetailDto
import ru.smartel.strike.dto.response.conflict.ConflictListDto
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.conflict.ConflictService

@RestController
@RequestMapping("/api/v2/{locale}/conflicts")
class ConflictController(
    private val conflictService: ConflictService,
) {
    @GetMapping
    fun index(
        conflictsRequest: ConflictListRequestDto, listRequest: BaseListRequestDto,
        @AuthenticationPrincipal user: UserPrincipal?
    ): ListWrapperDto<ConflictListDto> {
        return conflictService.list(conflictsRequest, listRequest, user)
    }

    @GetMapping("{id}")
    fun show(
        @PathVariable("locale") locale: Locale,
        @PathVariable("id") conflictId: Long,
    ): DetailWrapperDto<ConflictDetailDto> {
        return DetailWrapperDto(conflictService.get(conflictId, locale))
    }

    @PostMapping("{id}/favourites")
    fun setFavourite(
        @PathVariable("id") conflictId: Long,
        @RequestBody dto: FavouriteRequestDto,
        @AuthenticationPrincipal user: UserPrincipal?
    ) {
        user?.let {
            conflictService.setFavourite(conflictId, user.id, dto.favourite)
        }
    }
}
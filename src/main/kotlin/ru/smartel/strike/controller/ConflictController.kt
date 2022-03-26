package ru.smartel.strike.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.dto.request.conflict.ConflictFieldsDto
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.request.conflict.ConflictListRequestDto
import ru.smartel.strike.dto.request.conflict.ConflictReportRequestDto
import ru.smartel.strike.dto.request.conflict.FavouriteRequestDto
import ru.smartel.strike.dto.response.conflict.ConflictDetailDto
import ru.smartel.strike.dto.response.conflict.ConflictListDto
import ru.smartel.strike.dto.response.conflict.ConflictReportDto
import ru.smartel.strike.dto.response.reference.locality.LocalityDto
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.conflict.ConflictService
import java.time.LocalDateTime
import java.time.ZoneOffset

@RestController
@RequestMapping("/api/v2/{locale}/conflicts")
class ConflictController(
    private val conflictService: ConflictService,
) {
    @GetMapping
    fun index(
        @PathVariable("locale") locale: Locale,
        conflictsRequest: ConflictListRequestDto,
        listRequest: BaseListRequestDto,
        @AuthenticationPrincipal user: UserPrincipal?
    ): ListWrapperDto<ConflictListDto> {
        return conflictService.list(conflictsRequest, listRequest, locale, user)
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

    @GetMapping("report")
    fun report(dto: ConflictReportRequestDto): DetailWrapperDto<ConflictReportDto> {
        return DetailWrapperDto(
            conflictService.getReportByPeriod(
                LocalDateTime.ofEpochSecond(dto.from, 0, ZoneOffset.UTC).toLocalDate(),
                LocalDateTime.ofEpochSecond(dto.to, 0, ZoneOffset.UTC).toLocalDate(),
                dto.countriesIds
            )
        )
    }

    @GetMapping("{id}/latest-locality")
    fun latestLocality(
        @PathVariable("locale") locale: Locale,
        @PathVariable("id") conflictId: Long
    ): DetailWrapperDto<LocalityDto> {
        return DetailWrapperDto(conflictService.getLatestLocality(conflictId, locale))
    }

    @PostMapping
    fun store(
        @PathVariable("locale") locale: Locale,
        @RequestBody dto: ConflictFieldsDto,
        @AuthenticationPrincipal user: UserPrincipal?
    ): DetailWrapperDto<ConflictDetailDto> {
        return DetailWrapperDto(conflictService.create(dto, locale, user))
    }

    @PutMapping("{id}")
    fun update(
        @PathVariable("locale") locale: Locale,
        @PathVariable("id") conflictId: Long,
        @RequestBody dto: ConflictFieldsDto,
        @AuthenticationPrincipal user: UserPrincipal?
    ): DetailWrapperDto<ConflictDetailDto> {
        return DetailWrapperDto(conflictService.update(conflictId, dto, locale, user))
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") id: Long) {
        conflictService.delete(id)
    }
}
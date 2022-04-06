package ru.smartel.strike.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.dto.request.event.EventCreateRequestDto
import ru.smartel.strike.dto.request.event.EventListRequestDto
import ru.smartel.strike.dto.request.event.FavouriteRequestDto
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.response.event.EventDetailDto
import ru.smartel.strike.dto.response.event.EventListDto
import ru.smartel.strike.security.token.UserPrincipal
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.event.EventService

@RestController
@RequestMapping("/api/v2/{locale}/events")
@Validated
class EventController(
    private val eventService: EventService
) {
    @GetMapping
    fun index(
        @PathVariable("locale") locale: Locale,
        dto: EventListRequestDto,
        listRequest: BaseListRequestDto,
        @AuthenticationPrincipal user: UserPrincipal?
    ): ListWrapperDto<EventListDto> {
        return eventService.list(dto, listRequest, locale, user)
    }

    @GetMapping("{id}")
    fun show(
        @PathVariable("locale") locale: Locale,
        @PathVariable("id") id: Long,
        @RequestParam("withRelatives") withRelatives: Boolean,
        @AuthenticationPrincipal user: UserPrincipal?
    ): DetailWrapperDto<EventDetailDto> {
        return DetailWrapperDto(eventService.incrementViewsAndGet(id, locale, withRelatives, user))
    }

    @PostMapping("{id}/favourites")
    fun setFavourite(
        @PathVariable("id") id: Long,
        @RequestBody dto: FavouriteRequestDto,
        @AuthenticationPrincipal user: UserPrincipal?
    ) {
        user?.also {
            eventService.setFavourite(id, it.id, dto.favourite)
        }
    }

    @PostMapping
    fun store(
        @PathVariable("locale") locale: Locale,
        @RequestBody dto: EventCreateRequestDto,
        @AuthenticationPrincipal user: UserPrincipal?
    ): DetailWrapperDto<EventDetailDto> {
        return DetailWrapperDto(eventService.create(dto, locale, user))
    }
}
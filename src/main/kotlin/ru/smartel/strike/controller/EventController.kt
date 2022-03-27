package ru.smartel.strike.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.smartel.strike.dto.request.BaseListRequestDto
import ru.smartel.strike.dto.request.event.EventListRequestDto
import ru.smartel.strike.dto.response.ListWrapperDto
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

//    @PostMapping
//    fun create(
//        @PathVariable("locale") locale: Locale,
//        @RequestBody dto: CountryCreateRequestDto
//    ): DetailWrapperDto<CountryDetailDto> {
//        dto.locale = locale
//        return DetailWrapperDto(countryService.create(dto))
//    }
}
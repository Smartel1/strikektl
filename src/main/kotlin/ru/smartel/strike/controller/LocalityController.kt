package ru.smartel.strike.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.ListWrapperDto
import ru.smartel.strike.dto.request.reference.locality.LocalityCreateRequestDto
import ru.smartel.strike.dto.response.reference.locality.LocalityDetailDto
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.locality.LocalityService
import javax.validation.constraints.Size

@RestController
@RequestMapping("/api/v2/{locale}/localities")
@Validated
class LocalityController(
    private val localityService: LocalityService
) {
    @GetMapping
    fun list(
        @PathVariable("locale") locale: Locale,
        @RequestParam(value = "name", required = false) @Size(min = 2) name: String?,
        @RequestParam(value = "regionId", required = false) regionId: Int?
    ): ListWrapperDto<LocalityDetailDto> {
        return localityService.list(name, regionId, locale)
    }

    @PostMapping
    fun create(
        @PathVariable("locale") locale: Locale,
        @RequestBody dto: LocalityCreateRequestDto
    ): DetailWrapperDto<LocalityDetailDto> {
        dto.locale = locale
        return DetailWrapperDto(localityService.create(dto))
    }
}
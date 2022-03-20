package ru.smartel.strike.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.request.reference.region.RegionCreateRequestDto
import ru.smartel.strike.dto.response.reference.region.RegionDetailDto
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.region.RegionService
import javax.validation.constraints.Size

@RestController
@RequestMapping("/api/v2/{locale}/regions")
@Validated
class RegionController(
    private val regionService: RegionService
) {
    @GetMapping
    fun list(
        @PathVariable("locale") locale: Locale,
        @RequestParam(value = "name", required = false) @Size(min = 2) name: String?,
        @RequestParam(value = "countryId", required = false) countryId: Int?
    ): ListWrapperDto<RegionDetailDto> {
        return regionService.list(name, countryId, locale)
    }

    @PostMapping
    fun create(
        @PathVariable("locale") locale: Locale,
        @RequestBody dto: RegionCreateRequestDto
    ): DetailWrapperDto<RegionDetailDto> {
        dto.locale = locale
        return DetailWrapperDto(regionService.create(dto))
    }
}
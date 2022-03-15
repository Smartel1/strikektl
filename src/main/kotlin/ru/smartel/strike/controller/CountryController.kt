package ru.smartel.strike.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.ListWrapperDto
import ru.smartel.strike.dto.request.country.CountryCreateRequestDto
import ru.smartel.strike.dto.response.reference.country.CountryDetailDto
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.country.CountryService
import javax.validation.constraints.Size

@RestController
@RequestMapping("/api/v2/{locale}/countries")
@Validated
class CountryController(
    private val countryService: CountryService
) {
    @GetMapping
    fun list(
        @PathVariable("locale") locale: Locale,
        @RequestParam(value = "name", required = false) @Size(min = 2) name: String?
    ): ListWrapperDto<CountryDetailDto> {
        return countryService.list(name, locale)
    }

    @PostMapping
    fun create(
        @PathVariable("locale") locale: Locale,
        @RequestBody dto: CountryCreateRequestDto
    ): DetailWrapperDto<CountryDetailDto> {
        dto.locale = locale
        return DetailWrapperDto(countryService.create(dto))
    }
}
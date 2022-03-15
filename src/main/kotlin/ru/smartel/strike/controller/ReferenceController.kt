package ru.smartel.strike.controller

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.response.reference.ReferenceChecksumDto
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.reference.ReferenceService

@RestController
@RequestMapping("/api/v2/{locale}")
class ReferenceController(
    private val referenceService: ReferenceService,
) {
    @GetMapping("references")
    fun list(@PathVariable("locale") locale: Locale): DetailWrapperDto<Map<String, List<*>>> {
        return DetailWrapperDto(referenceService.getAllReferences(locale))
    }

    @ApiOperation(value = "Хэш-сумма справочников. Используется для того, чтобы отслеживать изменения и обновлять справочники в клиентском кэше только при необходимости")
    @GetMapping("references-checksum")
    fun checksum(): DetailWrapperDto<ReferenceChecksumDto> {
        return DetailWrapperDto(ReferenceChecksumDto(referenceService.getChecksum()))
    }
}
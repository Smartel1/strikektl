package ru.smartel.strike.controller

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import ru.smartel.strike.dto.DetailWrapperDto
import ru.smartel.strike.dto.ListWrapperDto
import ru.smartel.strike.dto.request.client_version.ClientVersionCreateRequestDto
import ru.smartel.strike.dto.request.client_version.ClientVersionGetNewRequestDto
import ru.smartel.strike.dto.response.client_version.ClientVersionDto
import ru.smartel.strike.service.Locale
import ru.smartel.strike.service.client_version.ClientVersionService

@RestController
@RequestMapping("/api/v2/{locale}/client-versions")
class ClientVersionController(
    private val clientVersionService: ClientVersionService
) {
    @ApiOperation(value = "Получить список новых версий")
    @GetMapping
    fun getNewVersions(request: ClientVersionGetNewRequestDto, @PathVariable locale: Locale):
            ListWrapperDto<ClientVersionDto> {
        return clientVersionService.getNewVersions(request, locale)
    }

    @ApiOperation(value = "Создать запись о версии приложения. clientId - уникальный идентификатор для приложения")
    @PostMapping(consumes = ["application/json"])
    fun store(@PathVariable locale: Locale, @RequestBody requestDTO: ClientVersionCreateRequestDto):
            DetailWrapperDto<ClientVersionDto> {
        return DetailWrapperDto(clientVersionService.create(requestDTO, locale));
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) {
        clientVersionService.delete(id)
    }
}
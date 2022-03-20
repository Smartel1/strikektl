package ru.smartel.strike.service.client_version

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import ru.smartel.strike.dto.response.ListWrapperDto
import ru.smartel.strike.dto.request.client_version.ClientVersionCreateRequestDto
import ru.smartel.strike.dto.request.client_version.ClientVersionGetNewRequestDto
import ru.smartel.strike.dto.response.client_version.ClientVersionDto
import ru.smartel.strike.entity.ClientVersionEntity
import ru.smartel.strike.exception.ValidationException
import ru.smartel.strike.repository.client_version.ClientVersionRepository
import ru.smartel.strike.service.Locale
import javax.persistence.EntityNotFoundException


@Service
class ClientVersionService(
    private val repository: ClientVersionRepository,
    private val dtoValidator: ClientVersionDtoValidator
) {
    fun getNewVersions(request: ClientVersionGetNewRequestDto, locale: Locale): ListWrapperDto<ClientVersionDto> {
        dtoValidator.validateListRequestDto(request)
        val newVersions = request.currentVersion?.let { v ->
            val currentVersion = repository.getByVersionAndClientId(v, request.clientId!!)
                ?: throw ValidationException(mapOf("error" to listOf("Нет такой версии: ${request.clientId}: $v")))
            repository.findAllByIdGreaterThanAndClientId(currentVersion.id, request.clientId)
        } ?: repository.findAllByClientId(request.clientId!!)

        return ListWrapperDto(newVersions.map { ClientVersionDto(it, locale) })
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun create(request: ClientVersionCreateRequestDto, locale: Locale): ClientVersionDto {
        dtoValidator.validateStoreDto(request)
        repository.getByVersionAndClientId(request.version!!, request.clientId!!)?.run {
            throw ValidationException(mapOf("error" to listOf("Такая версия уже существует")))
        }
        ClientVersionEntity(
            clientId = request.clientId,
            required = request.required!!,
            version = request.version,
            descriptionRu = request.descriptionRu!!,
            descriptionEn = request.descriptionEn!!,
            descriptionEs = request.descriptionEs!!,
            descriptionDe = request.descriptionDe!!
        )
            .also { repository.save(it) }
            .also { return ClientVersionDto(it, locale) }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun delete(id: Long) {
        repository.findById(id).ifPresentOrElse({ repository.delete(it) }) {
            throw EntityNotFoundException("Версия клиента не найдена")
        }
    }
}
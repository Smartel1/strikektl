package ru.smartel.strike.repository.client_version

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smartel.strike.entity.ClientVersionEntity

@Repository
interface ClientVersionRepository : JpaRepository<ClientVersionEntity, Long> {
    fun getByVersionAndClientId(version: String, clientId: String): ClientVersionEntity?
    fun findAllByIdGreaterThanAndClientId(id: Long, clientId: String): List<ClientVersionEntity>
    fun findAllByClientId(clientId: String): List<ClientVersionEntity>
}
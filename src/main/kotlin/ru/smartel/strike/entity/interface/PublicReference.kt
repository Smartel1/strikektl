package ru.smartel.strike.entity.`interface`

interface PublicReference {
    /**
     * Hash code of fields that are public. Changing of this fields in DB must impact on [publicHash]
     */
    fun publicHash(): Int
}
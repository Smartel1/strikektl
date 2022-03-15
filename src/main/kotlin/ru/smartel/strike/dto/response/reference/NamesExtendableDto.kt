package ru.smartel.strike.dto.response.reference

import com.fasterxml.jackson.annotation.JsonInclude
import ru.smartel.strike.entity.`interface`.HavingNames
import ru.smartel.strike.service.Locale

open class NamesExtendableDto(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    var name: String? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    var nameRu: String? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    var nameEn: String? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    var nameEs: String? = null,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    var nameDe: String? = null,
) {
    fun setNamesOf(entity: HavingNames, locale: Locale) {
        name = if (locale != Locale.ALL) entity.getNameByLocale(locale) else null
        nameRu = if (locale == Locale.ALL) entity.nameRu else null
        nameEn = if (locale == Locale.ALL) entity.nameEn else null
        nameEs = if (locale == Locale.ALL) entity.nameEs else null
        nameDe = if (locale == Locale.ALL) entity.nameDe else null
    }
}

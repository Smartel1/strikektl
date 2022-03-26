package ru.smartel.strike.dto.request.conflict

import io.swagger.annotations.ApiParam
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ConflictReportRequestDto(
    @ApiParam(value = "Начальная дата отчета", defaultValue = "0")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var from: Long = 0,
    @ApiParam(value = "Конечная дата отчета", defaultValue = "сегодня")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var to: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    @ApiParam(value = "Идентификаторы стран. В отчет попадут конфликты, " +
            "которые в отчетном периоде имеют события этих стран", defaultValue = "пустой список")
    var countriesIds: List<Long> = ArrayList()
)

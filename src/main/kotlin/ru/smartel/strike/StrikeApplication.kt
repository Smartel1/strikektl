package ru.smartel.strike

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class StrikeApplication

fun main(args: Array<String>) {
	runApplication<StrikeApplication>(*args)
}

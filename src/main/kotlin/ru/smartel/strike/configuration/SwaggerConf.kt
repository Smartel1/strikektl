package ru.smartel.strike.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.AuthenticatedPrincipal
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Collectors

@Configuration
@EnableSwagger2
class SwaggerConf {
    @Bean
    fun api() = Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
                .apis(RequestHandlerSelectors.basePackage("ru.smartel.strike.controller"))
                .paths(PathSelectors.any())
                .build()
            .genericModelSubstitutes(Optional::class.java)
            .ignoredParameterTypes(AuthenticatedPrincipal::class.java)

    fun apiInfo() = ApiInfo(
        "Thebestcom API Documentation",
        BufferedReader(
            InputStreamReader(SwaggerConf::class.java.classLoader.getResourceAsStream("doc.html")!!)
        ).lines().collect(Collectors.joining(System.lineSeparator())),
        "2.0",
        "",
        Contact("Andrew Silutin", "", "javablackstack@gmail.com"),
        "",
        "",
        emptyList())
}
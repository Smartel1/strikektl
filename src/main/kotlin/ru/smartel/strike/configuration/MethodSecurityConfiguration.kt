package ru.smartel.strike.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import ru.smartel.strike.security.authorization.CustomMethodSecurityExpressionHandler

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class MethodSecurityConfiguration(private val handler: CustomMethodSecurityExpressionHandler) :
    GlobalMethodSecurityConfiguration() {

    override fun createExpressionHandler() = handler
}
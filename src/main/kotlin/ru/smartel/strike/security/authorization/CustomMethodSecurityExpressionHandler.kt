package ru.smartel.strike.security.authorization

import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import ru.smartel.strike.repository.event.EventRepository
import ru.smartel.strike.repository.news.NewsRepository

@Component
class CustomMethodSecurityExpressionHandler(
    private val eventRepository: EventRepository,
    private val newsRepository: NewsRepository
) : DefaultMethodSecurityExpressionHandler() {
    private val trustResolver = AuthenticationTrustResolverImpl()

    override fun createSecurityExpressionRoot(
        authentication: Authentication,
        invocation: MethodInvocation
    ): MethodSecurityExpressionOperations {
        val root = CustomMethodSecurityExpressionRoot(authentication, eventRepository, newsRepository)
        root.setPermissionEvaluator(permissionEvaluator)
        root.setTrustResolver(this.trustResolver)
        root.setRoleHierarchy(roleHierarchy)
        return root
    }
}
package ru.smartel.strike.security.authorization

import org.springframework.security.access.expression.SecurityExpressionRoot
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
import org.springframework.security.core.Authentication
import ru.smartel.strike.repository.event.EventRepository
import ru.smartel.strike.repository.news.NewsRepository

class CustomMethodSecurityExpressionRoot(
    authentication: Authentication,
    private val eventRepository: EventRepository,
    private val newsRepository: NewsRepository
) : SecurityExpressionRoot(authentication), MethodSecurityExpressionOperations {

    var filterObject: Any? = null
        @JvmName("_getFilterObject") get
        @JvmName("_getFilterObject") set
    var returnObject: Any? = null
        @JvmName("_getReturnObject") get
        @JvmName("_getReturnObject") set

//    fun isEventAuthor(eventId: Long?): Boolean {
//        return if (principal !is UserPrincipal) false else eventRepository.findById(eventId)
//            .map(PostEntity::getAuthor)
//            .map(User::getId)
//            .map { authorId -> authorId.equals((principal as UserPrincipal).getId()) }
//            .orElse(java.lang.Boolean.FALSE)
//    }
//
//    fun isNewsAuthor(newsId: Long?): Boolean {
//        return if (principal !is UserPrincipal) false else newsRepository.findById(newsId)
//            .map(PostEntity::getAuthor)
//            .map(User::getId)
//            .map { authorId -> authorId.equals((principal as UserPrincipal).getId()) }
//            .orElse(java.lang.Boolean.FALSE)
//    }

    override fun getFilterObject() = filterObject
    override fun setFilterObject(filterObject: Any?) {
        this.filterObject = filterObject
    }

    override fun getReturnObject() = returnObject
    override fun setReturnObject(returnObject: Any?) {
        this.returnObject = returnObject
    }

    override fun getThis() = this
}
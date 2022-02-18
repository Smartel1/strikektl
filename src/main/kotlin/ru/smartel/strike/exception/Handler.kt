package ru.smartel.strike.exception

import org.hibernate.exception.ConstraintViolationException
import org.postgresql.util.PSQLException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import pl.exsio.nestedj.ex.InvalidNodesHierarchyException
import ru.smartel.strike.dto.exception.ApiErrorDto
import javax.persistence.EntityNotFoundException


@ControllerAdvice
class Handler {
    private val logger: Logger = LoggerFactory.getLogger(Handler::class.java)

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    fun processMissingServletRequestParameterException(ex: MissingServletRequestParameterException): ApiErrorDto? {
        logger.info("MissingServletRequestParameterException occurred: {}", ex.message)
        return ApiErrorDto("Validation failed", ex.message)
    }

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    fun processValidationException(ex: ValidationException): ApiErrorDto? {
        logger.info("ValidationException occurred: {}", ex.errors)
        return ApiErrorDto("Validation failed", ex.errors!!.entries
            .map { (key, value): Map.Entry<String, List<String?>?> ->
                "$key: " + java.lang.String.join(
                    ",",
                    value
                )
            })
    }

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun processEntityNotFoundException(ex: EntityNotFoundException): ApiErrorDto? {
        logger.info("EntityNotFoundException occurred: {}", ex.message)
        return ApiErrorDto("Entity not found", ex.message)
    }

    @ExceptionHandler(BindException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun processBindException(ex: BindException): ApiErrorDto? {
        logger.warn("BindException occurred: {}", ex.message)
        return ApiErrorDto("Binding failed",
            ex.getBindingResult().getFieldErrors()
                .map { err ->
                    "binding to '" + err.getField().toString() + "' failed, rejected value: " + err.getRejectedValue()
                })
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    fun processException(ex: AccessDeniedException): ApiErrorDto? {
        logger.warn("AccessDeniedException occurred")
        return ApiErrorDto("Forbidden", ex.message)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    fun processException(ex: DataIntegrityViolationException): ApiErrorDto? {
        if (null != ex.cause && ex.cause is ConstraintViolationException) {
            if (null != (ex.cause as ConstraintViolationException).cause && (ex.cause as ConstraintViolationException).cause is PSQLException) {
                val message = (ex.cause as ConstraintViolationException).cause!!.message
                logger.warn("Constraint violated: {}", message)
                return ApiErrorDto("Constraint violated", message)
            }
        }
        logger.warn("Data violation exception occurred: {}", ex.message)
        return ApiErrorDto("Data violation exception", ex.message)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    fun processMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException?): ApiErrorDto? {
        logger.warn("Not supported method", ex)
        return ApiErrorDto("Request method not supported", listOf())
    }

    @ExceptionHandler(InvalidNodesHierarchyException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    fun processMethodNotSupportedException(ex: InvalidNodesHierarchyException): ApiErrorDto? {
        logger.warn("Nodes hierarchy error: {}", ex.message)
        return ApiErrorDto("Nodes hierarchy error", ex.message)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun argumentMismatchException(ex: MethodArgumentTypeMismatchException): ApiErrorDto {
        logger.warn("Exception occurred", ex)
        return ApiErrorDto("incorrect value '${ex.value}' for '${ex.name}'", listOf())
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun processException(ex: Exception): ApiErrorDto {
        logger.warn("Exception occurred", ex)
        return ApiErrorDto("Server error (sorry)", listOf())
    }
}
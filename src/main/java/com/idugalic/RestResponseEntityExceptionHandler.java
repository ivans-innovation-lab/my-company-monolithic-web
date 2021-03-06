package com.idugalic;

import com.idugalic.commandside.blog.aggregate.exception.PublishBlogPostException;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.model.ConcurrencyException;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * A general exception handler for the application.
 * It maps exception type to a response.
 * You can override handlers from @ResponseEntityExceptionHandler, for example handleMethodArgumentNotValid.
 *
 * @author idugalic
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    public RestResponseEntityExceptionHandler() {
        super();
    }

    // ########## Override of ResponseEntityExceptionHandler ############

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status,
                                                                  final WebRequest request) {

        return new ResponseEntity<Object>(ex.getBindingResult().getFieldErrors().stream().map(v -> {
            JSR303Violation error = new JSR303Violation();
            error.type = JSR303ViolationType.REQUEST_VIOLATION;
            error.message = v.getDefaultMessage();
            error.propertyPath = v.getField();
            error.className = v.getObjectName();
            return error;
        }).collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
    }

    // ########## CommandExecution exceptions ##########

    @ExceptionHandler({CommandExecutionException.class})
    protected ResponseEntity<Object> handleCommandExecution(final RuntimeException cex, final WebRequest request) {
        final String bodyOfResponse = "CommandExecutionException";
        if (null != cex.getCause()) {
            LOG.error("CAUSED BY: {} {}", cex.getCause().getClass().getName(), cex.getCause().getMessage());

            if (cex.getCause() instanceof ConcurrencyException) {
                return handleExceptionInternal(cex, bodyOfResponse + " - Concurrency issue", new HttpHeaders(), HttpStatus.CONFLICT, request);
            }
        }
        return handleExceptionInternal(cex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({InvalidDataAccessApiUsageException.class, DataAccessException.class})
    protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "DataAccessException";
        LOG.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
        final String bodyOfResponse = "DataIntegrityViolationException";
        LOG.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "Internal Error";
        LOG.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        final String bodyOfResponse = "Access denied";
        LOG.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    // ########## Custom exception handling ##########

    @ExceptionHandler({PublishBlogPostException.class})
    public ResponseEntity<Object> handleBadRequest(final PublishBlogPostException ex, final WebRequest request) {
        final String bodyOfResponse = "PublishBlogPostException";
        LOG.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // ########## JSR303 - Command Validation error handling ##########

    @ExceptionHandler({JSR303ViolationException.class})
    public ResponseEntity<Object> handleValidation(final JSR303ViolationException ex, final WebRequest request) {
        LOG.error("Validation error", ex);
        return new ResponseEntity<Object>(ex.getViolations().stream().map(v -> {
            JSR303Violation error = new JSR303Violation();
            error.type = JSR303ViolationType.COMMAND_VIOLATION;
            error.message = v.getMessage();
            error.propertyPath = v.getPropertyPath().toString();
            error.className = v.getRootBeanClass().getSimpleName();
            return error;
        }).collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
    }

    class JSR303Violation implements Serializable {
        public String message;
        public String propertyPath;
        public String className;
        public JSR303ViolationType type;
    }

    enum JSR303ViolationType {
        REQUEST_VIOLATION,
        COMMAND_VIOLATION
    }

}

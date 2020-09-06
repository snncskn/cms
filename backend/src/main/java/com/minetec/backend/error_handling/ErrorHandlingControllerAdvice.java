package com.minetec.backend.error_handling;

import com.minetec.backend.dto.RestResponse;
import com.minetec.backend.error_handling.exception.AccessNotAllowedException;
import com.minetec.backend.error_handling.exception.BadRequestException;
import com.minetec.backend.error_handling.exception.ErrorOccurredException;
import com.minetec.backend.error_handling.exception.ItemNotFoundException;
import com.minetec.backend.error_handling.exception.StockException;
import com.minetec.backend.util.RestResponseFactory;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.sql.SQLException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorHandlingControllerAdvice {

    private static final String REFERENCED_VIOLATION_STATE = "23503";
    private static final String DUPLICATED_VIOLATION_STATE = "23505";
    private static final String VARYING_VIOLATION_STATE = "22001";

    private final RestResponseFactory resp; return new ResponseEntity<>(resp.error(code, messageText, ex.getMessage()), HttpStatus.BAD_REQUEST);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestResponse> defaultExceptionHandler(final Throwable ex,
                                                                final HttpServletRequest request,
                                                                final HttpServletResponse response) {

        if (response == null) {
            log.debug("An exception occurred", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.error(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(
            resp.error("c01d5b769dfa", "error-occurred", ex.getLocalizedMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<RestResponse> handleException(final AccessDeniedException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Access not Allowed", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(resp.error("588f8e09a330",
            "access-not-allowed", ex.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AccessNotAllowedException.class)
    public ResponseEntity<RestResponse> handleException(final AccessNotAllowedException ex,
                                                        final HttpServletRequest request,
                                                        final HttpServletResponse response) {

        if (response == null) {
            log.debug("Access not allowed", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        final String exceptionMessage = ex.getMessage();
        final String messageText = exceptionMessage != null ? exceptionMessage : "access-not-allowed";
        final String code = ex.getCode() == null ? "504a995889df" : ex.getCode();

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(resp.error(code, messageText, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<RestResponse> handleException(final ExpiredJwtException ex,
                                                        final HttpServletRequest request,
                                                        final HttpServletResponse response) {

        if (response == null) {
            log.debug("expired-jwt", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        try {
            response.sendError(HttpStatus.FORBIDDEN.value());
        } catch (IOException e) {
            log.error(ErrorUtils.exceptionToString(ex, request));
        }


        final String exceptionMessage = ex.getMessage();
        final String messageText = exceptionMessage != null ? exceptionMessage : "expired-jwt";

        return new ResponseEntity<>(resp.error("va1a88ckc219", messageText, ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<RestResponse> handleException(final BadRequestException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Bad request", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        final String exceptionMessage = ex.getMessage();
        final String messageText = exceptionMessage != null ? exceptionMessage : "bad-request";
        final String code = ex.getCode() == null ? "aa5a48cdc383" : ex.getCode();

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(resp.error(code, messageText, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    ResponseEntity<RestResponse> handleException(final ConversionFailedException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Conversion failed", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(
            resp.error("bb94e55cfe36", "item-not-found", ex.getMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ErrorOccurredException.class)
    ResponseEntity<RestResponse> handleException(final ErrorOccurredException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("An error occurred", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        final String exceptionMessage = ex.getMessage();
        final String messageText = exceptionMessage != null ? exceptionMessage : "error-occurred";
        final String code = ex.getCode() == null ? "7ff7336b4b99" : ex.getCode();

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(resp.error(messageText, code, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<RestResponse> handleException(final HttpMessageNotReadableException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Bad request", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(
            resp.error("8b26b15cef94", "bad-request", ex.getMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<RestResponse> handleException(final HttpRequestMethodNotSupportedException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Wrong request method", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.info(ErrorUtils.exceptionToString(ex, request));

        final RestResponse restResponse = resp.error("ed99d9ed4be8", "method-not-supported");

        return new ResponseEntity<>(restResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    ResponseEntity<RestResponse> handleException(final HttpMediaTypeNotAcceptableException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Wrong mime type", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.info(ErrorUtils.exceptionToString(ex, request));

        final RestResponse restResponse = resp.error("9677321f8637", "method-not-supported");

        return new ResponseEntity<>(restResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    ResponseEntity<RestResponse> handleException(final ItemNotFoundException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Item not found", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        final String exceptionMessage = ex.getMessage();
        final String messageText = exceptionMessage != null ? exceptionMessage : "item-not-found";
        final String code = ex.getCode() == null ? "ed4ff56d7c0a" : ex.getCode();

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(resp.error(code, messageText, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<RestResponse> handleException(final MethodArgumentNotValidException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Invalid method argument", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity(resp.error("ebdc9d7bcc91", "validation-error",
            ErrorUtils.exceptionToString(ex)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<RestResponse> handleException(final MissingServletRequestParameterException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Invalid method argument", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");
        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(
            resp.error("d67930a04798", "validation-error", ex.getParameterName()), HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Returns form validation errors from controller validations
     *
     * @param ex Thrown validation exception
     * @return REST response
     */
    @ExceptionHandler(ValidationException.class)
    ResponseEntity<RestResponse> handleException(final ValidationException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Invalid request", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(
            resp.error("a9a3629f207a", "validation-error", ex.getMessage()), HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<RestResponse> handleException(final DataIntegrityViolationException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Invalid request", ex);
            return null;
        }

        log.info(ErrorUtils.exceptionToString(ex, request));

        // Overriding response type
        response.setContentType("application/json");

        if (ex.getRootCause() instanceof SQLException) {
            SQLException sqlException = (SQLException) ex.getRootCause();

            if (REFERENCED_VIOLATION_STATE.equals(sqlException.getSQLState())) {
                return new ResponseEntity<>(
                    resp.error("v9a4123f109k", "referenced-violation",
                        sqlException.getMessage()), HttpStatus.BAD_REQUEST
                );
            } else if (DUPLICATED_VIOLATION_STATE.equals(sqlException.getSQLState())) {
                return new ResponseEntity<>(
                    resp.error("a9a4529f107a", "duplicated-violation",
                        sqlException.getMessage()), HttpStatus.BAD_REQUEST
                );
            } else if (VARYING_VIOLATION_STATE.equals(sqlException.getSQLState())) {
                return new ResponseEntity<>(
                    resp.error("a9a4529f107a", "varying-violation",
                        sqlException.getMessage()), HttpStatus.BAD_REQUEST
                );
            }
        }

        return new ResponseEntity<>(
            resp.error("u1a7529f318s", "violation-exception",
                ex.getCause().getCause().getMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(StockException.class)
    ResponseEntity<RestResponse> handleException(final StockException ex,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        if (response == null) {
            log.debug("Stock not allowed", ex);
            return null;
        }

        // Overriding response type
        response.setContentType("application/json");

        log.info(ErrorUtils.exceptionToString(ex, request));

        return new ResponseEntity<>(resp.error("298f8g39a320",
            "stock-not-allowed", ex.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

}

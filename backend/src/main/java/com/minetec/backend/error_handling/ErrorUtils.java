package com.minetec.backend.error_handling;

import com.minetec.backend.error_handling.exception.BaseException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

final class ErrorUtils {

    private static final String STACK_TRACE_SEPARATOR = "Stack Trace ---------------------------------------\n";

    private ErrorUtils() {

    }

    static String exceptionToString(final Throwable ex, final HttpServletRequest request) {

        return requestToString(request) +
            STACK_TRACE_SEPARATOR +
            ExceptionUtils.getStackTrace(ex);
    }

    static String exceptionToString(final BaseException ex, final HttpServletRequest request) {

        return requestToString(request) +
            STACK_TRACE_SEPARATOR +
            "Code: " + ex.getCode() + "\n" +
            ExceptionUtils.getStackTrace(ex);
    }

    static String exceptionToString(
        final ConversionFailedException ex, final HttpServletRequest request
    ) {

        return requestToString(request) +
            STACK_TRACE_SEPARATOR +
            "Source Type: " + ex.getSourceType() + "\n" +
            "Target Type: " + ex.getTargetType() + "\n" +
            "Value: " + ex.getValue() + "\n" +
            ExceptionUtils.getStackTrace(ex);
    }

    static String exceptionToString(
        final HttpRequestMethodNotSupportedException ex, final HttpServletRequest request
    ) {

        return requestToString(request) +
            STACK_TRACE_SEPARATOR +
            "Method: " + ex.getMethod() + "\n" +
            "Supported HTTP Methods: " + ex.getSupportedHttpMethods() + "\n" +
            ExceptionUtils.getStackTrace(ex);
    }

    static String exceptionToString(
        final MethodArgumentNotValidException ex, final HttpServletRequest request
    ) {

        final StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(requestToString(request));
        errorMessage.append(STACK_TRACE_SEPARATOR);

        final BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) ex.getBindingResult();
        final List<ObjectError> errors = bindingResult.getAllErrors();

        for (ObjectError error : errors) {
            errorMessage.append(error.toString()).append("\n");
        }

        errorMessage.append(ExceptionUtils.getStackTrace(ex));

        return errorMessage.toString();
    }

    static String exceptionToString(final MethodArgumentNotValidException ex) {

        final StringBuilder errorMessage = new StringBuilder();
        final BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) ex.getBindingResult();
        final List<ObjectError> errors = bindingResult.getAllErrors();

        errors.forEach(objectError -> {
            errorMessage.append(objectError.getCodes()[0]).append(" ");
            errorMessage.append(objectError.getDefaultMessage());
        });

        return errorMessage.toString();
    }

    static String exceptionToString(
        final MissingServletRequestParameterException ex, final HttpServletRequest request
    ) {

        return requestToString(request) +
            STACK_TRACE_SEPARATOR +
            "Missing Parameter Name: " + ex.getParameterName() + "\n" +
            "Missing Parameter's Type: " + ex.getParameterType() + "\n" +
            ExceptionUtils.getStackTrace(ex);
    }

    private static String requestToString(final HttpServletRequest request) {

        final StringBuilder errorMessage = new StringBuilder();
        final String requestURI = request.getRequestURI();

        errorMessage
            .append("\n")
            .append("Request -------------------------------------------\n")
            .append("Path: ").append(requestURI).append("\n")
            .append("Method: ").append(request.getMethod()).append("\n");

        final String queryString = request.getQueryString();

        if (queryString != null) {

            errorMessage
                .append("Query String: ").append(queryString).append("\n")
                .append("Parameters: ")
                .append(
                    Collections.list(request.getParameterNames())
                        .stream()
                        .map(pn -> pn + ":" + request.getParameter(pn))
                        .collect(Collectors.toList())
                )
                .append("\n");
        }

        errorMessage.append("Headers -------------------------------------------\n");

        final Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            final String headerContent = request.getHeader(headerName);

            errorMessage.append(headerName).append(": ").append(headerContent).append("\n");
        }

        try {

            final String content = request
                .getReader()
                .lines()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

            errorMessage.append("Content -------------------------------------------\n").append(content);
        } catch (Exception e) {
            // pass
        }

        return errorMessage.toString();
    }
}

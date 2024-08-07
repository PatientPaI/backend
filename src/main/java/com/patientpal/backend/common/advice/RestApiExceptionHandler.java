package com.patientpal.backend.common.advice;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.common.exception.ErrorResponse;
import com.patientpal.backend.webhook.service.DiscordWebhookService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class, basePackages = "com.patientpal.backend")
public class RestApiExceptionHandler {
    private final Optional<DiscordWebhookService> discordWebhookService;

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        var response = ErrorResponse.of(ErrorCode.AUTHENTICATION_FAILED);
        log.debug("Authentication failed: {}", e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        var response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        log.debug("Parameter type is invalid: {}", e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
        log.debug("Argument validation has failed: {}", e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        var response = ErrorResponse.of(e.getErrorCode());
        log.error("Unexpected error has occurred: {}", e.getDetail());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, WebRequest request) {
        // FIXME: 추후에 모니터링 환경이 갖추어지면 제거될 예정
        discordWebhookService.ifPresent(service -> service.sendDiscordAlarm(e, request));
        var response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestCookieException(MissingRequestCookieException e) {
        var response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        log.debug("Required cookie is missing: {}", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}

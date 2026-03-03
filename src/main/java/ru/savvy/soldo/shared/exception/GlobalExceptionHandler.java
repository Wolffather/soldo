package ru.savvy.soldo.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.savvy.soldo.shared.exception.ErrorResponse;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── 400 Bad Request ───────────────────────────────────

    /**
     * Ошибки валидации @Valid — возвращаем ошибки по каждому полю
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                                                               fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Ошибка валидации")
                .message("Проверьте корректность переданных данных")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Недопустимая бизнес-операция
     * (например, подтверждение уже отменённого бронирования)
     */
    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<ErrorResponse> handleIllegalOperation(
            IllegalOperationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Недопустимая операция", ex);
    }

    /**
     * Отсутствует обязательный заголовок (например, X-Bot-Secret)
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(
            MissingRequestHeaderException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Отсутствует заголовок", ex);
    }

    // ─── 403 Forbidden ────────────────────────────────────

    /**
     * Нет прав доступа (Spring Security AccessDeniedException)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Доступ запрещён", ex);
    }

    // ─── 404 Not Found ────────────────────────────────────

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Не найдено", ex);
    }

    // ─── 409 Conflict ─────────────────────────────────────

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleRoleExists(
            RoleAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Конфликт данных", ex);
    }

    @ExceptionHandler(DataDuplicationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DataDuplicationException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Дубликат данных", ex);
    }

    // ─── 500 Internal Server Error ────────────────────────

    /**
     * Всё, что не поймали выше — логируем и возвращаем
     * безопасное сообщение без деталей
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Непредвиденная ошибка", ex);

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Внутренняя ошибка сервера")
                .message("Произошла непредвиденная ошибка. Обратитесь к администратору.")
                .build();

        return ResponseEntity.internalServerError().body(response);
    }

    // ─── Утилитарный метод ────────────────────────────────

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String error, Exception ex) {

        ErrorResponse response = ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
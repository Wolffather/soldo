package ru.savvy.soldo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;
    private String error;
    private String message;

    // Только для ошибок валидации: поле → сообщение
    private Map<String, String> fieldErrors;
}
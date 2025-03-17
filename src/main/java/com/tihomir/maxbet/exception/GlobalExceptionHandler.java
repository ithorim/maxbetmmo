package com.tihomir.maxbet.exception;

import com.tihomir.maxbet.dto.ApiErrorDto;
import com.tihomir.maxbet.exception.custom.CharacterClassNotFoundException;
import com.tihomir.maxbet.exception.custom.CharacterNotFoundException;
import com.tihomir.maxbet.exception.custom.InvalidItemTransferException;
import com.tihomir.maxbet.exception.custom.ItemNotFoundException;
import com.tihomir.maxbet.service.CharacterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.data.redis.serializer.SerializationException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final CharacterService characterService;

    @ExceptionHandler(CharacterNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleCharacterNotFoundException(
            CharacterNotFoundException e, WebRequest request) {

        log.warn("Character not found: " + e.getMessage());

        ApiErrorDto apiErrorDto = ApiErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiErrorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CharacterClassNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleCharacterClassNotFoundException(
            CharacterClassNotFoundException e, WebRequest request) {

        log.warn("Character class not found: " + e.getMessage());

        ApiErrorDto apiErrorDto = ApiErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiErrorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleItemNotFoundException(
            ItemNotFoundException e, WebRequest request) {

        log.warn("Item not found: " + e.getMessage());

        ApiErrorDto apiErrorDto = ApiErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiErrorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidItemTransferException.class)
    public ResponseEntity<ApiErrorDto> handleInvalidItemTransferException(
            InvalidItemTransferException e, WebRequest request) {

        log.warn("Invalid item transfer attempted: " + e.getMessage());

        ApiErrorDto apiErrorDto = ApiErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiErrorDto, HttpStatus.BAD_REQUEST);
    }

    // handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationException(
            MethodArgumentNotValidException e, WebRequest request) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: " + message);

        ApiErrorDto apiErrorDto = ApiErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(message)
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiErrorDto, HttpStatus.BAD_REQUEST);
    }

    // exception for uncaught errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGlobalException(
            Exception e, WebRequest request) {

        log.warn("An unexpected error occurred: " + e.getMessage());

        ApiErrorDto apiErrorDto = ApiErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SerializationException.class)
    public ResponseEntity<ApiErrorDto> handleSerializationException(
            SerializationException e, WebRequest request) {

        log.error("Serialization error: " + e.getMessage(), e);
        
        // clear cache to fix the issue for future requests
        characterService.clearCache();

        ApiErrorDto apiErrorDto = ApiErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An error occurred while processing your request. Please try again.")
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

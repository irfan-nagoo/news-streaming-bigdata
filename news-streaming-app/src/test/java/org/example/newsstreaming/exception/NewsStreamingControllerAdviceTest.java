package org.example.newsstreaming.exception;

import org.example.newsstreaming.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NewsStreamingControllerAdviceTest {

    @InjectMocks
    private NewsStreamingControllerAdvice controllerAdvice;

    @Test
    void handleRecordNotFoundException() {
        RecordNotFoundException exception = new RecordNotFoundException("Record Not Found");
        ResponseEntity<ErrorResponse> response = controllerAdvice.handleRecordNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Record Not Found", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Input argument Invalid");
        ResponseEntity<ErrorResponse> response = controllerAdvice.handleIllegalArgumentException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Input argument Invalid", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void handleException() {
        Exception exception = new Exception("Processing Error has Occurred");
        ResponseEntity<ErrorResponse> response = controllerAdvice.handleException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Processing error has occurred", Objects.requireNonNull(response.getBody()).getMessage());
    }
}
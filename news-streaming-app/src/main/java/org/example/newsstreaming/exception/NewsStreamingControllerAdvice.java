package org.example.newsstreaming.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.newsstreaming.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.example.newsstreaming.constants.MessagingConstants.PROCESSING_ERROR_MSG;

/**
 * @author irfan.nagoo
 */

@ControllerAdvice
@Slf4j
public class NewsStreamingControllerAdvice {

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecordNotFoundException(RecordNotFoundException nfe) {
        log.error("Record not found: ", nfe);
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.name(), nfe.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException nfe) {
        log.error("Bad request parameter: ", nfe);
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.name(), nfe.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Processing error occurred: ", e);
        return ResponseEntity.internalServerError().body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(),
                PROCESSING_ERROR_MSG));
    }

}

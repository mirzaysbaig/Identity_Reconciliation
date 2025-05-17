package Identity.reconcilliation.bitespeed.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import Identity.reconcilliation.bitespeed.dto.IdentifyResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<IdentifyResponse> handleInvalidRequest(InvalidRequestException ex) {
        IdentifyResponse response = new IdentifyResponse();
        return ResponseEntity.badRequest().body(response);
    }
}

package wisoft.tddstart.commerce.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvalidCommandExceptionHandler {

    @ExceptionHandler(InvalidCommandException.class)
    public ResponseEntity<?> handle() {
        return ResponseEntity.badRequest().build();
    }
}

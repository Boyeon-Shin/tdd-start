package wisoft.tddstart.commerce.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wisoft.tddstart.commerce.commandModel.InvalidCommandException;

@ControllerAdvice
public class InvalidCommandExceptionHandler {

    @ExceptionHandler(InvalidCommandException.class)
    public ResponseEntity<?> handle() {
        return ResponseEntity.badRequest().build();
    }
}

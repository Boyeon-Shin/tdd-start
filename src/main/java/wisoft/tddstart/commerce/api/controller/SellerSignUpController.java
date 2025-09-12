package wisoft.tddstart.commerce.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.command.CreateSellerCommand;

@RestController
public record SellerSignUpController() {

    @PostMapping("/seller/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerCommand command) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        String usernameRegex = "^[a-zA-Z0-9_-]*$";

        if (command.email() != null && command.email().contains("@") != false && !command.email().endsWith("@")
                && command.email().matches(emailRegex) != false && command.username() != null && !command.username()
                .isBlank() && command.username().length() >= 3 && command.username().matches(usernameRegex) != false
                && command.password() != null) {
            if (command.password().length() < 8) {
                return ResponseEntity.badRequest().build();
            } else {
                return ResponseEntity.noContent().build();
            }
        } else {
    return ResponseEntity.badRequest().build();
}
    }
}

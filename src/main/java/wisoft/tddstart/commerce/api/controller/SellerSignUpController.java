package wisoft.tddstart.commerce.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.command.CreateSellerCommand;

@RestController
public record SellerSignUpController() {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String USERNAME_REGEX = "[a-zA-Z0-9_-]+$";


    @PostMapping("/seller/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerCommand command) {
        if (isCommandValid(command)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    private static boolean isCommandValid(final CreateSellerCommand command) {
        return isEmailValid(command.email())
                && isUserNameValid(command.username())
                && isPasswordValid(command.password());
    }

    private static boolean isEmailValid(final String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    private static boolean isUserNameValid(final String username) {
        return username != null && username.matches(USERNAME_REGEX);
    }

    private static boolean isPasswordValid(final String password) {
        return password != null && password.length() >= 8;
    }
}

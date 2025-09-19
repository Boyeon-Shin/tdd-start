package wisoft.tddstart.commerce.api.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Seller;
import wisoft.tddstart.commerce.SellerRepository;
import wisoft.tddstart.commerce.command.CreateSellerCommand;

@RestController
public record SellerSignUpController(PasswordEncoder passwordEncoder, SellerRepository sellerRepository) {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String USERNAME_REGEX = "[a-zA-Z0-9_-]{3,}$";


    @PostMapping("/seller/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerCommand command) {
        if (isCommandValid(command) == false) {
            return ResponseEntity.badRequest().build();
        }

        String hashedPassword = passwordEncoder.encode(command.password());

        var seller = new Seller();
        seller.setEmail(command.email());
        seller.setUsername(command.username());
        seller.setHashedPassword(hashedPassword);

        try {
            sellerRepository.save(seller);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
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

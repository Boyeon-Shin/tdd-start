package wisoft.tddstart.commerce.api.controller;

import static wisoft.tddstart.commerce.UserPropertyValidator.isEmailValid;
import static wisoft.tddstart.commerce.UserPropertyValidator.isPasswordValid;
import static wisoft.tddstart.commerce.UserPropertyValidator.isUserNameValid;

import java.util.UUID;
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


    @PostMapping("/seller/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateSellerCommand command) {
        if (!isCommandValid(command)) {
            return ResponseEntity.badRequest().build();
        }

        String hashedPassword = passwordEncoder.encode(command.password());

        var seller = new Seller();
        UUID id = UUID.randomUUID();
        seller.setId(id);
        seller.setEmail(command.email());
        seller.setUsername(command.username());
        seller.setHashedPassword(hashedPassword);
        sellerRepository.save(seller);

        return ResponseEntity.noContent().build();
    }

    private static boolean isCommandValid(final CreateSellerCommand command) {
        return isEmailValid(command.email())
                && isUserNameValid(command.username())
                && isPasswordValid(command.password());
    }
}

package wisoft.tddstart.commerce.api.controller;

import static wisoft.tddstart.commerce.UserPropertyValidator.isEmailValid;
import static wisoft.tddstart.commerce.UserPropertyValidator.isPasswordValid;
import static wisoft.tddstart.commerce.UserPropertyValidator.isUserNameValid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Shopper;
import wisoft.tddstart.commerce.ShopperRepository;
import wisoft.tddstart.commerce.command.CreateShopperCommand;

@RestController
public record ShopperSignController(PasswordEncoder passwordEncoder, ShopperRepository repository) {

    @PostMapping("/shopper/signUp")
    ResponseEntity<?> signUp(@RequestBody CreateShopperCommand command) {
        if (!isCommandValid(command)) {
            return ResponseEntity.badRequest().build();
        }

        String hashedPassword = passwordEncoder.encode(command.password());
        var shopper = new Shopper();
        shopper.setEmail(command.email());
        shopper.setUserName(command.username());
        shopper.setHashedPassword(hashedPassword);

        repository.save(shopper);

        return ResponseEntity.noContent().build();
    }

    private static boolean isCommandValid(final CreateShopperCommand command) {
        return isEmailValid(command.email())
                && isUserNameValid(command.username())
                && isPasswordValid(command.password());
    }
}



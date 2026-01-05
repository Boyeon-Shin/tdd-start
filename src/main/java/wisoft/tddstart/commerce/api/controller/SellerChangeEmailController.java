package wisoft.tddstart.commerce.api.controller;

import static wisoft.tddstart.commerce.UserPropertyValidator.isEmailValid;

import java.security.Principal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Seller;
import wisoft.tddstart.commerce.SellerRepository;
import wisoft.tddstart.commerce.command.ChangeContactEmailCommand;

@RestController
public record SellerChangeEmailController(SellerRepository repository) {

    @PostMapping("/seller/changeContactEmail")
    ResponseEntity<?> changeContactEmail(
            @RequestBody ChangeContactEmailCommand command,
            Principal user
    ) {
        if(isEmailValid(command.contactEmail()) == false) {
            return ResponseEntity.badRequest().build();
        }

        UUID id = UUID.fromString(user.getName());
        Seller seller = repository.findById(id).orElseThrow();
        seller.setContactEmail(command.contactEmail());
        repository.save(seller);

        return ResponseEntity.noContent().build();
    }


}

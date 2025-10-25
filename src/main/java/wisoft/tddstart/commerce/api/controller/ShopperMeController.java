package wisoft.tddstart.commerce.api.controller;

import java.security.Principal;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Shopper;
import wisoft.tddstart.commerce.ShopperRepository;
import wisoft.tddstart.commerce.view.ShopperMeView;

@RestController
public record ShopperMeController (ShopperRepository repository) {
    @GetMapping("/shopper/me")
    ShopperMeView me(Principal user) {
        UUID id =  UUID.fromString(user.getName());
        Shopper shopper = repository.findById(id).orElseThrow();
        return new ShopperMeView(id, shopper.getEmail(), shopper.getUserName());
    }
}

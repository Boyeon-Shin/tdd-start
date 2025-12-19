package wisoft.tddstart.commerce.api.controller;

import java.security.Principal;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Seller;
import wisoft.tddstart.commerce.SellerRepository;
import wisoft.tddstart.commerce.view.SellerMeView;

@RestController
public record SellerMeController(SellerRepository sellerRepository) {

    @GetMapping("/seller/me")
    SellerMeView me(Principal user) {
        UUID id = UUID.fromString(user.getName());
        Seller seller = sellerRepository.findById(id).orElseThrow();
        return new SellerMeView(id, seller.getEmail(), seller.getUsername(), null);
    }
}

package wisoft.tddstart.commerce.api.controller;

import java.security.Principal;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.view.SellerMeView;

@RestController
public class SellerMeController {

    @GetMapping("/seller/me")
    SellerMeView me(Principal user) {
        UUID id = UUID.fromString(user.getName());
        return new SellerMeView(id, null, null);
    }



}

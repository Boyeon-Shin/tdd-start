package wisoft.tddstart.commerce.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.result.AccessTokenCarries;

@RestController
public class SellerIssueTokenController {

    @PostMapping("/seller/issueToken")
    AccessTokenCarries issueToken() {
        return new AccessTokenCarries("token");
    }
}

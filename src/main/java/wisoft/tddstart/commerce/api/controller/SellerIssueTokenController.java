package wisoft.tddstart.commerce.api.controller;

import io.jsonwebtoken.Jwts;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.SellerRepository;
import wisoft.tddstart.commerce.query.IssueSellerToken;
import wisoft.tddstart.commerce.result.AccessTokenCarries;

@RestController
public record SellerIssueTokenController(
        SellerRepository repository,
        PasswordEncoder passwordEncoder,
        @Value("${jwt.secret}") String jwtSecret
) {
    @PostMapping("/seller/issueToken")
    ResponseEntity<?> issueToken(@RequestBody IssueSellerToken query) {
        return repository
                 .findByEmail(query.email())
                .filter(seller -> passwordEncoder.matches(query.password(), seller.getHashedPassword()))
                .map(seller -> composeToken())
                .map(AccessTokenCarries::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private String composeToken() {
        return Jwts
                .builder()
                .signWith(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256"))
                .compact();
    }
}



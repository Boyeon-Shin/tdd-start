package wisoft.tddstart.commerce.api.controller;

import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Seller;
import wisoft.tddstart.commerce.SellerRepository;
import wisoft.tddstart.commerce.api.JwtKeyHolder;
import wisoft.tddstart.commerce.query.IssueSellerToken;
import wisoft.tddstart.commerce.result.AccessTokenCarrier;

@RestController
public record SellerIssueTokenController(
        SellerRepository repository,
        PasswordEncoder passwordEncoder,
        JwtKeyHolder jwtKeyHolder
//        @Value("${jwt.secret}") String jwtSecret
) {
    @PostMapping("/seller/issueToken")
    ResponseEntity<?> issueToken(@RequestBody IssueSellerToken query) {
        return repository
                 .findByEmail(query.email())
                .filter(seller -> passwordEncoder.matches(query.password(), seller.getHashedPassword()))
                .map(seller -> composeToken(seller))
                .map(AccessTokenCarrier::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private String composeToken(Seller seller) {
        return Jwts
                .builder()
                .setSubject(seller.getId().toString())
                .claim("scp", "seller")
                .signWith(jwtKeyHolder.key())
                .compact();
    }
}

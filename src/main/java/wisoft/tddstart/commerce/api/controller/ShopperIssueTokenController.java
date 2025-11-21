package wisoft.tddstart.commerce.api.controller;

import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Shopper;
import wisoft.tddstart.commerce.ShopperRepository;
import wisoft.tddstart.commerce.api.JwtKeyHolder;
import wisoft.tddstart.commerce.query.IssueShopperToken;
import wisoft.tddstart.commerce.result.AccessTokenCarrier;

@RestController
public record ShopperIssueTokenController(ShopperRepository repository, PasswordEncoder passwordEncoder, JwtKeyHolder jwtKeyHolder) {


    @PostMapping("/shopper/issueToken")
    ResponseEntity<?> issueToken(@RequestBody IssueShopperToken query) {
        return repository
                .findByEmail(query.email())
                .filter(shopper -> passwordEncoder.matches(
                        query.password(),
                        shopper.getHashedPassword()
                ))
                .map(shopper -> composeToken(shopper))
                .map(AccessTokenCarrier::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private String composeToken(Shopper shopper) {
        return Jwts
                .builder()
                .setSubject(shopper.getId().toString())
                .claim("scp" , "shopper")
                .signWith(jwtKeyHolder.key())
                .compact();
    }
}

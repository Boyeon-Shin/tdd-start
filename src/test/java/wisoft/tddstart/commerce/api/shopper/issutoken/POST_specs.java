package wisoft.tddstart.commerce.api.shopper.issutoken;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static wisoft.tddstart.EmailGenerator.generateEmail;
import static wisoft.tddstart.JwtAssertion.conformsToJwtFormat;
import static wisoft.tddstart.PasswordGenerator.generatePassword;
import static wisoft.tddstart.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.command.CreateShopperCommand;
import wisoft.tddstart.commerce.query.IssueShopperToken;
import wisoft.tddstart.commerce.result.AccessTokenCarrier;

@CommerceApiTest
@DisplayName("POST /shopper/issueToken")
public class POST_specs {

    @Test
    void 올바르게_요청하면_200_OK_상태코드와_접근_토큰을_반환한다(@Autowired TestRestTemplate client) {
        String email = generateEmail();
        String password = generatePassword();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(email, generateUsername(), password), Void.class);

        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, password),
                AccessTokenCarrier.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotNull();
    }


    @Test
    void 접근_토큰은_JWT_형식을_따른다(@Autowired TestRestTemplate client) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(email, generateUsername(), password),
                Void.class
        );

        // Act
        ResponseEntity<AccessTokenCarrier> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, password),
                AccessTokenCarrier.class
        );

        // Assert
        String actual = requireNonNull(response.getBody()).accessToken();
        assertThat(actual).satisfies(conformsToJwtFormat());

    }


    @Test
    void 존재하지_않는_이메일_주소가_사용되면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, password),
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }



    @Test
    void 잘못된_비밀번호가_사용되면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestRestTemplate client
    ) {
        // Arrange
        String email = generateEmail();
        String password = generatePassword();
        String wrongPassword = generatePassword();

        client.postForEntity(
                "/shopper/signUp",
                new CreateShopperCommand(email, generateUsername(), password),
                Void.class
        );

        // Act
        ResponseEntity<Void> response = client.postForEntity(
                "/shopper/issueToken",
                new IssueShopperToken(email, wrongPassword),
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

}

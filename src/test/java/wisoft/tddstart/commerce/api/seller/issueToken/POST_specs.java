package wisoft.tddstart.commerce.api.seller.issueToken;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static wisoft.tddstart.EmailGenerator.generateEmail;
import static wisoft.tddstart.JwtAssertion.conformsToJwtFormat;
import static wisoft.tddstart.PasswordGenerator.generatePassword;
import static wisoft.tddstart.UsernameGenerator.generateUsername;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.command.CreateSellerCommand;
import wisoft.tddstart.commerce.query.IssueSellerToken;
import wisoft.tddstart.commerce.result.AccessTokenCarries;

@CommerceApiTest
@DisplayName("POST /seller/issueToken")
public class POST_specs {

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        String email = generateEmail();
        String password = generatePassword();

        //Arrange
        client.postForEntity("/seller/signUp",
                new CreateSellerCommand(email, generateUsername(), password),
                Void.class);

        //Act
        ResponseEntity<AccessTokenCarries> response = client.postForEntity(
                "/seller/issueToken",
                new IssueSellerToken(email, password),
                AccessTokenCarries.class);

        //Assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
    }


    @Test
    void 올바르게_요청하면_접근_토큰을_반환한다(@Autowired TestRestTemplate client) {
        String email = generateEmail();
        String password = generatePassword();

        //Arrange
        client.postForEntity("/seller/signUp",
                new CreateSellerCommand(email, generateUsername(), password),
                Void.class);

        //Act
        ResponseEntity<AccessTokenCarries> response = client.postForEntity(
                "/seller/issueToken",
                new IssueSellerToken(email, password),
                AccessTokenCarries.class);

        //Assert
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().accessToken()).isNotNull();
    }

    @Test
    void 접근_토큰은_JWT_형식을_따른다(@Autowired TestRestTemplate client) {

        String email = generateEmail();
        String password = generatePassword();

        //Arrange
        client.postForEntity("/seller/signUp",
                new CreateSellerCommand(email, generateUsername(), password),
                Void.class);

        //Act
        ResponseEntity<AccessTokenCarries> response = client.postForEntity(
                "/seller/issueToken",
                new IssueSellerToken(email, password),
                AccessTokenCarries.class);

        //Assert
        String actual = requireNonNull(response.getBody().accessToken());
        assertThat(actual).satisfies(conformsToJwtFormat());
    }

    @Test
    void 존재하지_않는_이메일_주소가_사용되면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {

        String email = generateEmail();
        String password = generatePassword();

        //Act
        ResponseEntity<AccessTokenCarries> response = client.postForEntity(
                "/seller/issueToken",
                new IssueSellerToken(email, password),
                AccessTokenCarries.class);

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @Test
    void 잘못된_비밀번호가_사용되면_400_Bad_Request_상태코드_를_반환한다(@Autowired TestRestTemplate client) {

        String email = generateEmail();
        String password = generatePassword();
        String wrongPassword = generatePassword();

        //Arrange
        client.postForEntity("/seller/signUp",
                new CreateSellerCommand(email, generateUsername(), password),
                Void.class);

        //Act
        ResponseEntity<AccessTokenCarries> response = client.postForEntity(
                "/seller/issueToken",
                new IssueSellerToken(email, wrongPassword),
                AccessTokenCarries.class);

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}





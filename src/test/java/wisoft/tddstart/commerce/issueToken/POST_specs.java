package wisoft.tddstart.commerce.issueToken;

import static wisoft.tddstart.EmailGenerator.generateEmail;
import static wisoft.tddstart.PasswordGenerator.generatePassword;
import static wisoft.tddstart.UsernameGenerator.generateUsername;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.TddStartApplication;
import wisoft.tddstart.commerce.command.CreateSellerCommand;
import wisoft.tddstart.commerce.query.IssueSellerToken;
import wisoft.tddstart.commerce.result.AccessTokenCarries;

@SpringBootTest(classes = TddStartApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
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
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().accessToken()).isNotNull();
        Assertions.assertThat(response.getBody().accessToken()).contains(".");
    }
}





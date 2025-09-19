package wisoft.tddstart.commerce.api.seller.signUp;

import static wisoft.tddstart.EmailGenerator.generateEmail;
import static wisoft.tddstart.PasswordGenerator.generatePassword;
import static wisoft.tddstart.UsernameGenerator.generateUsername;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import wisoft.tddstart.TddStartApplication;
import wisoft.tddstart.commerce.Seller;
import wisoft.tddstart.commerce.SellerRepository;
import wisoft.tddstart.commerce.command.CreateSellerCommand;

@SpringBootTest(
        classes = TddStartApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DisplayName("POST/seller/signUp")
public class POST_specs {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        //arrange
        var command = new CreateSellerCommand(generateEmail(), generateUsername(), "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        //arrange
        var command = new CreateSellerCommand(null, generateUsername(), "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "invalid-email@",
            "invalid-email@test",
            "invalid-email@test.",
            "invalid-email@.com"
    })
    void email_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(String email, @Autowired TestRestTemplate client) {

        var command = new CreateSellerCommand(email, generateUsername(), "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @Test
    void username_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        //arrange
        var command = new CreateSellerCommand(generateEmail(), null, "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "se",
            "seller.",
            "seller!",
            "seller@"
    })
    void username_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(String username, @Autowired TestRestTemplate client) {
        //arrange
        var command = new CreateSellerCommand(generateEmail(), username, "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "seller",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "0123456789",
            "seller_",
            "seller-"
    })
    void username_속성이_올바른_형식에_따르면_204_No_Content_상태코드를_반환한다(String username, @Autowired TestRestTemplate client) {
        var command = new CreateSellerCommand(generateEmail(), username, "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(204);
    }


    @Test
    void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        var command = new CreateSellerCommand(generateEmail(), generateUsername(), null);

        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "pass",
            "pass123"
    })
    void password_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(String password, @Autowired TestRestTemplate client) {
        var command = new CreateSellerCommand(generateEmail(), generateUsername(), password);

        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void email_속성에_이미_존재하는_이메일_주소가_지정되면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        String email = generateEmail();

        client.postForEntity("/seller/signUp",
                new CreateSellerCommand(email, generateUsername(), "password"), Void.class);

        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                new CreateSellerCommand(email, generateUsername(), "password"), Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @Test
    void username_속성에_이미_존재하는_사용자이름이_지정되면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        String username = generateUsername();

        client.postForEntity("/seller/signUp",
                new CreateSellerCommand(generateEmail(), username, "password"), Void.class);

        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                new CreateSellerCommand(generateEmail(), username, "password"), Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @Test
    void 비밀번호를_올바르게_암호화한다(@Autowired TestRestTemplate client,
                          @Autowired SellerRepository sellerRepository,
                          @Autowired PasswordEncoder encoder) {

        var command = new CreateSellerCommand(generateEmail(), generateUsername(), generatePassword());

        //act
        client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Seller seller = sellerRepository
                        .findAll()
                        .stream()
                        .filter(x -> x.getEmail().equals(command.email()))
                        .findFirst()
                        .orElseThrow();

        String actual = seller.getHashedPassword();

        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(encoder.matches(command.password(), actual)).isTrue();
    }
}



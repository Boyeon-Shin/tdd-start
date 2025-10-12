package wisoft.tddstart.commerce.api.shopper.signUp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static wisoft.tddstart.EmailGenerator.generateEmail;
import static wisoft.tddstart.PasswordGenerator.generatePassword;
import static wisoft.tddstart.UsernameGenerator.generateUsername;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import wisoft.tddstart.commerce.Shopper;
import wisoft.tddstart.commerce.ShopperRepository;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.command.CreateShopperCommand;


@CommerceApiTest
@DisplayName("POST /shopper/signUp")
public class POST_specs {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        var command = new CreateShopperCommand(generateEmail(), generateUsername(),
                generatePassword());

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp", command, Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(204);
    }


    @Test
    void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {

        var command = new CreateShopperCommand(null, generateUsername(), generatePassword());

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp", command, Void.class);

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

        var command = new CreateShopperCommand(
                email,
                generateUsername(),
                generatePassword());

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp", command, Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @Test
    void username_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {

        var command = new CreateShopperCommand(generateEmail(), null, generatePassword());

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp", command, Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);

    }


    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "sh",
            "shopper ",
            "shopper.",
            "shopper!",
            "shopper@",
    })
    void username_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(String username, @Autowired TestRestTemplate client) {
        var command = new CreateShopperCommand(generateEmail(), username, generatePassword());

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp", command, Void.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "abcdefghijklmnopqrstuvwxyz",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "0123456789",
            "seller_",
            "seller-"
    })
    void username_속성이_올바른_형식을_따르면_204_No_Content_상태코드를_반환한다(String username, @Autowired TestRestTemplate client) {
        var command = new CreateShopperCommand(generateEmail(), username, generatePassword());

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp", command, Void.class);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }


    @Test
    void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        var command = new CreateShopperCommand(generateEmail(), generateUsername(), null);

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp", command, Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @MethodSource("wisoft.tddstart.commerce.TestDataSource#invalidPasswords")
    void password_속성이_올바른_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(String password, @Autowired TestRestTemplate client) {
        var command = new CreateShopperCommand(generateEmail(), generateUsername(), password);

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp", command, Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void email_속성에_이미_존재하는_이메일_주소가_지정되면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {

        String email = generateEmail();

        client.postForEntity("/shopper/signUp", new CreateShopperCommand(email, generateUsername(), generatePassword()),
                Void.class);

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp",
                new CreateShopperCommand(email, generateUsername(), generatePassword()), Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void username_속성이_이미_존재하는_사용자이름이_지정되면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {

        String username = generateUsername();

        client.postForEntity("/shopper/signUp", new CreateShopperCommand(generateEmail(), username, generatePassword()),
                Void.class);

        ResponseEntity<Void> response = client.postForEntity("/shopper/signUp",
                new CreateShopperCommand(generateEmail(), username, generatePassword()), Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @Test
    void 비밀번호를_올바르게_암호화한다(@Autowired TestRestTemplate client,
                          @Autowired ShopperRepository repository,
                          @Autowired PasswordEncoder encoder
    ) {
        //Arrange
        var command = new CreateShopperCommand(generateEmail(), generateUsername(), generatePassword());

        //Act
        client.postForEntity("/shopper/signUp", command, Void.class);

        //Assert
        Shopper shopper = repository
                .findAll()
                .stream()
                .filter(x -> x.getEmail().equals(command.email()))
                .findFirst()
                .orElseThrow();

        String actual = shopper.getHashedPassword();
        assertThat(actual).isNotNull();
        assertThat(encoder.matches(command.password(), actual)).isTrue();
    }

}




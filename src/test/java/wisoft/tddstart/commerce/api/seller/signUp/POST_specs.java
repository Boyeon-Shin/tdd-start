package wisoft.tddstart.commerce.api.seller.signUp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.TddStartApplication;
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
        var command = new CreateSellerCommand("seller@test.com", "seller", "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        //arrange
        var command = new CreateSellerCommand(null, "seller", "password");

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

        var command = new CreateSellerCommand(email, "seller", "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }


    @Test
    void username_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        //arrange
        var command = new CreateSellerCommand("seller@test.com", null, "password");

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
        var command = new CreateSellerCommand("seller@test.com", username, "password");

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
        var command = new CreateSellerCommand("seller@test.com", username, "password");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(204);
    }


    @Test
    void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        var command = new CreateSellerCommand("seller@test.com", "seller", null);

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
        var command = new CreateSellerCommand("seller@test.com", "seller", password);

        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void email_속성에_이미_존재하는_이메일_주소가_지정되면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        String email = "seller@test.com";

        client.postForEntity("/seller/signUp",
                new CreateSellerCommand(email, "seller", "password"), Void.class);

        ResponseEntity<Void> response = client.postForEntity(
                "/seller/signUp",
                new CreateSellerCommand(email, "seller", "password"), Void.class);

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    }



}



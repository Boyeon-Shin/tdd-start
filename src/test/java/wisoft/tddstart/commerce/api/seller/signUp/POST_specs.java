package wisoft.tddstart.commerce.api.seller.signUp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        var command = new CreateSellerCommand("seller@test.com", "seller", "passeord");

        //act
        ResponseEntity<Void> response = client.postForEntity("/seller/signUp", command, void.class);

        //assert
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(204);
    }



}

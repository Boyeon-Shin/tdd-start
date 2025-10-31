package wisoft.tddstart.commerce.api.seller.products;

import static org.assertj.core.api.Assertions.assertThat;
import static wisoft.tddstart.RegisterProductCommandGenerator.generateRegisterProductCommand;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.TestFixture;
import wisoft.tddstart.commerce.api.CommerceApiTest;

@CommerceApiTest
@DisplayName("POST /seller/products")
public class POST_specs {

    @Test
    void 올바르게_요청하면_201_Created_상태코드를_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();

        ResponseEntity<Void> response = fixture.client().postForEntity(
                "/seller/products",
                generateRegisterProductCommand(),
                Void.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }
}

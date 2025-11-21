package wisoft.tddstart.commerce.api.shopper.products;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.TestFixture;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.result.PageCarrier;
import wisoft.tddstart.commerce.view.ProductView;

import static org.assertj.core.api.Assertions.assertThat;
import static  org.springframework.http.RequestEntity.get;

@CommerceApiTest
@DisplayName("GET /shopper/products")
public class GET_specs {

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(@Autowired TestFixture fixture) {
        //Arrange
        fixture.createShopperThenSetAsDefaultUser();

        //Act
        ResponseEntity<PageCarrier<ProductView>> response= fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() {}
        );

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void 판매자_접근_토큰을_사용하면_403_Forbidden_상태코드를_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();

        ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }





}

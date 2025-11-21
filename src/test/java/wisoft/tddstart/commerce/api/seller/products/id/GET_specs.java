package wisoft.tddstart.commerce.api.seller.products.id;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static wisoft.tddstart.ProductAssertions.isDerivedForm;
import static wisoft.tddstart.RegisterProductCommandGenerator.generateRegisterProductCommand;

import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.coyote.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.TestFixture;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.command.RegisterProductCommand;
import wisoft.tddstart.commerce.view.ArrayCarrier;
import wisoft.tddstart.commerce.view.SellerProductView;

@CommerceApiTest
@DisplayName("GET /seller/product/{id}")
public class GET_specs {

    @Test
    void 올바르게_요쳥하면_200_OK_상태코드를_반환한다(@Autowired TestFixture fixture) {
        //Arrange
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();

        //Act
        ResponseEntity<?> response = fixture.client().getForEntity(
                "/seller/products/" + id,
                SellerProductView.class
        );

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void 판매자가_아닌_사용자의_접근_토큰을_사용하면_403_Forbidden_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();

        fixture.createShopperThenSetAsDefaultUser();

        ResponseEntity<?> response = fixture.client().getForEntity(
                "/seller/products/" + id,
                SellerProductView.class
        );
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(403);
    }

    @Test
    void 존재하지_않는_상품_식별자를_사용하면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();

        ResponseEntity<?> response = fixture.client().getForEntity(
                "/seller/products/" + id,
                SellerProductView.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }


    @Test
    void 다른_판매자가_등록한_상품_식별자를_사용하면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();

        fixture.createSellerThenSetAsDefaultUser();

        ResponseEntity<?> response = fixture.client().getForEntity(
                "/seller/products/" + id,
                SellerProductView.class
        );
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void 상품_식별자를_올바르게_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        UUID id = fixture.registerProduct();
        System.out.println("test id" + id);

        SellerProductView actual = fixture.client().getForObject(
                "/seller/products/" + id,
                SellerProductView.class
        );

        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(id);
    }


    @Test
    void 상품_정보를_올바르게_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        RegisterProductCommand command = generateRegisterProductCommand();
        UUID id = fixture.registerProduct(command);

        SellerProductView actual = fixture.client().getForObject(
                "/seller/products/" + id,
                SellerProductView.class
        );

        assertThat(actual).satisfies(isDerivedForm(command));
    }


    @Test
    void 상품_등록_시각을_올바르게_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        LocalDateTime referenceTime = LocalDateTime.now(UTC);
        UUID id = fixture.registerProduct();


        SellerProductView actual = fixture.client().getForObject(
                "/seller/products/" + id,
                SellerProductView.class
        );

        assertThat(actual.registeredTimeUtc()).isCloseTo(referenceTime, within(1, SECONDS));
    }
}

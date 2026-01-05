package wisoft.tddstart.commerce.api.seller.products;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Comparator.reverseOrder;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.springframework.http.RequestEntity.get;
import static wisoft.tddstart.commerce.ProductAssertions.isDerivedForm;
import static wisoft.tddstart.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.naming.Referenceable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.commerce.api.TestFixture;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.command.RegisterProductCommand;
import wisoft.tddstart.commerce.result.ArrayCarrier;
import wisoft.tddstart.commerce.view.SellerProductView;

@CommerceApiTest
@DisplayName("GET /seller/products")
public class GET_specs {

    @Autowired
    private Referenceable referenceable;

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(@Autowired TestFixture fixture) {
        //Arrange
        fixture.createSellerThenSetAsDefaultUser();

        //Act
        ResponseEntity<ArrayCarrier<SellerProductView>> response = fixture.client().exchange(
                RequestEntity.get("/seller/products").build(),
                new ParameterizedTypeReference<>() {
                }
        );

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }


    @Test
    void 판매자가_등록한_모든_상품을_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        List<UUID> ids = fixture.registerProducts();

        ResponseEntity<ArrayCarrier<SellerProductView>> response =
                fixture.client().exchange(
                        RequestEntity.get("/seller/products").build(),
                        new ParameterizedTypeReference<>() {
                        }
                );

        ArrayCarrier<SellerProductView> actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.items()).extracting(SellerProductView::id).containsAll(ids);
    }


    @Test
    void 다른_판매자가_등록한_상품이_포함되지_않는다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        UUID unexpected = fixture.registerProduct();

        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProduct();

        ResponseEntity<ArrayCarrier<SellerProductView>> response =
                fixture.client().exchange(
                        get("/seller/products").build(),
                        new ParameterizedTypeReference<>() { }
                );

        Assertions.assertThat(requireNonNull(response.getBody()).items())
                .extracting(SellerProductView::id)
                .doesNotContain(unexpected);
    }

    @Test
    void 상품_정보를_올바르게_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        RegisterProductCommand command = generateRegisterProductCommand();
        fixture.registerProduct(command);

        ResponseEntity<ArrayCarrier<SellerProductView>> response =
                fixture.client().exchange(
                        get("/seller/products").build(),
                        new ParameterizedTypeReference<>() { }
                );

        ArrayCarrier<SellerProductView> body = response.getBody();
        SellerProductView actual = requireNonNull(body).items()[0];
        assertThat(actual).satisfies(isDerivedForm(command));
    }

    @Test
    void 상품_등록_시각을_올바르게_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        LocalDateTime referenceTime = LocalDateTime.now(UTC);
        fixture.registerProduct();

        ResponseEntity<ArrayCarrier<SellerProductView>> response =
                fixture.client().exchange(
                        get("/seller/products").build(),
                        new ParameterizedTypeReference<>() { }
                );

        ArrayCarrier<SellerProductView> body = response.getBody();
        SellerProductView actual = requireNonNull(body).items()[0];
        Assertions.assertThat(actual.registeredTimeUtc()).isCloseTo(referenceTime, Assertions.within(1, SECONDS));
    }


    @Test
    void 상품_목록을_등록_시점_역순으로_정렬한다(@Autowired TestFixture fixture) {
        //Arrange
        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProducts();

        //Act
        ResponseEntity<ArrayCarrier<SellerProductView>> response =
                fixture.client().exchange(
                        get("/seller/products").build(),
                        new ParameterizedTypeReference<>() { }
                );

        System.out.println("here" + response.getBody());

        //Assert
        assertThat(requireNonNull(response.getBody()).items())
                .extracting(SellerProductView::registeredTimeUtc)
                .isSortedAccordingTo(reverseOrder());
    }
}

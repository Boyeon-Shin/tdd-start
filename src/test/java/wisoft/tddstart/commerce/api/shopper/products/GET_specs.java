package wisoft.tddstart.commerce.api.shopper.products;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static wisoft.tddstart.commerce.ProductAssertions.isViewDerivedFrom;
import static wisoft.tddstart.commerce.RegisterProductCommandGenerator.generateRegisterProductCommand;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.commerce.api.TestFixture;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.command.RegisterProductCommand;
import wisoft.tddstart.commerce.result.PageCarrier;
import wisoft.tddstart.commerce.view.ProductView;
import wisoft.tddstart.commerce.view.SellerMeView;
import wisoft.tddstart.commerce.view.SellerView;

@CommerceApiTest
@DisplayName("GET /shopper/products")
public class GET_specs {

    private static final int PAGE_SIZE = 10;

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(@Autowired TestFixture fixture) {
        //Arrange
        fixture.createShopperThenSetAsDefaultUser();

        //Act
        ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() {
                }
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


    @Test
    void 첫_번째_페이지의_상품을_반환한다(@Autowired TestFixture fixture) {
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        List<UUID> ids = fixture.registerProducts(PAGE_SIZE);
        fixture.createShopperThenSetAsDefaultUser();

        ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() {
                }
        );

        PageCarrier<ProductView> actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.items()).extracting(ProductView::id).containsAll(ids);
    }

    @Test
    void 상품_목록을_등록_시점_역순으로_정렬한다(@Autowired TestFixture fixture) {

        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        UUID ids1 = fixture.registerProduct();
        UUID ids2 = fixture.registerProduct();
        UUID ids3 = fixture.registerProduct();

        fixture.createShopperThenSetAsDefaultUser();

        ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(requireNonNull(response.getBody()).items())
                .extracting(ProductView::id)
                .containsExactly(ids3, ids2, ids1);

    }

    @Test
    void 상품_정보를_올바르게_반환한다(@Autowired TestFixture fixture) {

        fixture.deleteAllProducts();
        fixture.createSellerThenSetAsDefaultUser();

        RegisterProductCommand command = generateRegisterProductCommand();
        fixture.registerProduct(command);

        fixture.createShopperThenSetAsDefaultUser();

        ResponseEntity<PageCarrier<ProductView>> response = fixture.client().exchange(
                get("/shopper/products").build(),
                new ParameterizedTypeReference<>() {
                }
        );

        ProductView actual = requireNonNull(response.getBody()).items()[0];
        assertThat(actual).satisfies(isViewDerivedFrom(command));
    }


    //상품 등록의 명령이 테스트에 노출될 필요 없음, 판매자 정보를 가져오는 것이 필요함
    @Test
    void 판매자_정보를_올바르게_반환한다(@Autowired TestFixture fixture) {
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        SellerMeView seller = fixture.getSeller();
        fixture.registerProduct();

        fixture.createShopperThenSetAsDefaultUser();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
                fixture.client().exchange(
                        get("/shopper/products").build(),
                        new ParameterizedTypeReference<>() {
                        }
                );

        PageCarrier<ProductView> body = response.getBody();
        SellerView actual = requireNonNull(body).items()[0].seller();
        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(seller.id());
        assertThat(actual.username()).isEqualTo(seller.username());
    }


    @Test
    void 두_번째_페이지를_올바르게_반환한다(@Autowired TestFixture fixture) {
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProducts(PAGE_SIZE / 2);
        List<UUID> ids = fixture.registerProducts(PAGE_SIZE);
        fixture.registerProducts(PAGE_SIZE);

        fixture.createShopperThenSetAsDefaultUser();
        String token = fixture.consumeProductPage();

        // Act
        ResponseEntity<PageCarrier<ProductView>> response =
                fixture.client().exchange(
                        get("/shopper/products?continuationToken=" + token).build(),
                        new ParameterizedTypeReference<>() { }
                );

        // Assert
        assertThat(requireNonNull(response.getBody()).items())
                .extracting(ProductView::id)
                .containsExactlyElementsOf(ids.reversed());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, PAGE_SIZE })
    void 마지막_페이지를_올바르게_반환한다(int lastPageSize, @Autowired TestFixture fixture) {
        //Arrange
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        List<UUID> ids = fixture.registerProducts(lastPageSize);
        fixture.registerProducts(PAGE_SIZE * 2);

        fixture.createShopperThenSetAsDefaultUser();
        String token = fixture.consumeTwoProductPages();

        //Act
        ResponseEntity<PageCarrier<ProductView>> response =
                fixture.client().exchange(
                        get("/shopper/products?continuationToken=" + token).build(),
                        new ParameterizedTypeReference<>() { }
                );

        // Assert
        PageCarrier<ProductView> actual = response.getBody();
        assertThat(requireNonNull(requireNonNull(actual)).items())
                .extracting(ProductView::id)
                .containsExactlyElementsOf(ids.reversed());
        assertThat(actual.continuationToken()).isNull();
    }

    @Test
    void continuationToken_매개변수에_빈_문자열이_지정되면_첫_번째_페이지를_반환한다(@Autowired TestFixture fixture) {
        //Arrange
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        fixture.registerProducts(PAGE_SIZE);
        List<UUID> ids = fixture.registerProducts(PAGE_SIZE);
        fixture.createShopperThenSetAsDefaultUser();


        //Act
        ResponseEntity<PageCarrier<ProductView>> response =
                fixture.client().exchange(
                        get("/shopper/products?continuationToken=" ).build(),
                        new ParameterizedTypeReference<>() { }
                );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(requireNonNull(requireNonNull(response.getBody())).items())
                .extracting(ProductView::id)
                .containsAll(ids);
    }


    @Test
    void  문의_이메일_주소를_올바르게_설정한다(@Autowired TestFixture fixture) {
        fixture.deleteAllProducts();

        fixture.createSellerThenSetAsDefaultUser();
        SellerMeView seller = fixture.getSeller();
        fixture.registerProduct();

        fixture.createShopperThenSetAsDefaultUser();

        ResponseEntity<PageCarrier<ProductView>> response =
                fixture.client().exchange(
                        get("/shopper/products").build(),
                        new ParameterizedTypeReference<>() {}
                );

        ProductView actual = requireNonNull(response.getBody()).items()[0];
        assertThat(actual.seller().contactEmail()).isEqualTo(seller.contactEmail());
    }
}

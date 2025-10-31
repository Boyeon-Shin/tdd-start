package wisoft.tddstart.commerce.api.seller.products;

import static org.assertj.core.api.Assertions.assertThat;
import static wisoft.tddstart.RegisterProductCommandGenerator.generateRegisterProductCommand;
import static wisoft.tddstart.RegisterProductCommandGenerator.generateRegisterProductCommandWithImageUri;

import java.net.URI;
import java.util.UUID;
import java.util.function.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Test
    void 판매자가_아닌_사용자의_접근_토큰을_사용하면_403_Forbidden_상태코드를_반환한다(@Autowired TestFixture fixture) {

        fixture.createShopperThenSetAsDefaultUser();

        ResponseEntity<Void> response = fixture.client().postForEntity(
                "/seller/products",
                generateRegisterProductCommand(),
                Void.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(403);

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invaild-uri",
            "http://",
            "://missing-scheme.com"
    })
    void imageUri_속성이_URI_형식을_따르지_않으면_400_Bad_Request_상태코드를_반환한다(String imageUri,@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();

        ResponseEntity<Void> response = fixture.client().postForEntity(
                "/seller/products",
                generateRegisterProductCommandWithImageUri(imageUri),
                Void.class
        );
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
    
    @Test
    void 올바르게_요청하면_등록된_상품_정보에_접근하는_Location_헤더를_반환한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        
        ResponseEntity<Void> response = fixture.client().postForEntity(
                "/seller/products",
                generateRegisterProductCommand(),
                Void.class);

        URI actual = response.getHeaders().getLocation();
        assertThat(actual).isNotNull();
        assertThat(actual.getPath()).startsWith("/seller/products").matches(endsWithUUID());
        assertThat(actual.isAbsolute()).isFalse();
    }

    private Predicate<? super String> endsWithUUID() {
        return path -> {
            String[] segments = path.split("/");
            String testSegment = segments[segments.length - 1];
            try {
                UUID.fromString(testSegment);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        };
    }
}

package wisoft.tddstart.commerce.api.shopper.me;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static wisoft.tddstart.commerce.EmailGenerator.generateEmail;
import static wisoft.tddstart.commerce.PasswordGenerator.generatePassword;
import static wisoft.tddstart.commerce.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.commerce.api.TestFixture;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.view.ShopperMeView;

@CommerceApiTest
@DisplayName("GET /shopper/me")
public class GET_specs {

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(@Autowired TestFixture fixture) {
        //Arrange
        String password = generatePassword();
        String email = generateEmail();

        fixture.createShopper(email, generateUsername(), password);

        String token = fixture.issueShopperToken(email, password);

        //Act
        ResponseEntity<ShopperMeView> response = fixture.client().exchange(
                get("/shopper/me")
                        .header("Authorization", "Bearer " + token)
                        .build(),
                ShopperMeView.class
        );

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }


    @Test
    void 접근_토큰을_사용하지_않으면_401_Unauthorized_상태코드를_반환한다(@Autowired TestFixture fixture) {
        ResponseEntity<Void> response = fixture.client().exchange(
                get("/shopper/me").build(),
                Void.class
        );
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }


    @Test
    void 서로_다른_구매자의_식별자는_서로_다르다(@Autowired TestFixture fixture) {
        //Arrange
        String token1 = fixture.createShopperThenIssueToken();
        String token2 = fixture.createShopperThenIssueToken();

        //Act
        ResponseEntity<ShopperMeView> response1 = fixture.client().exchange(
                get("/shopper/me")
                        .header("Authorization", "Bearer " + token1)
                        .build(),
                ShopperMeView.class
        );

        ResponseEntity<ShopperMeView> response2 = fixture.client().exchange(
                get("/shopper/me")
                        .header("Authorization", "Bearer " + token2)
                        .build(),
                ShopperMeView.class
        );

        //Assert
        assertThat(requireNonNull(response1.getBody()).id())
                .isNotEqualTo(requireNonNull(response2.getBody()).id());
    }


    @Test
    void 같은_구매자의_식별자는_항상_같다(@Autowired TestFixture fixture) {
        //Arrange
        String email = generateEmail();
        String password = generatePassword();

        fixture.createShopper(email, generateUsername(), password);

        String token1 = fixture.issueShopperToken(email, password);
        String token2 = fixture.issueShopperToken(email, password);

        //Act
        ResponseEntity<ShopperMeView> response1 = fixture.client().exchange(
                get("/shopper/me")
                        .header("Authorization", "Bearer " + token1)
                        .build(),
                ShopperMeView.class
        );

        ResponseEntity<ShopperMeView> response2 = fixture.client().exchange(
                get("/shopper/me")
                        .header("Authorization", "Bearer " + token2)
                        .build(),
                ShopperMeView.class
        );

        //Assert
        assertThat(requireNonNull(response1.getBody()).id())
                .isEqualTo(requireNonNull(response2.getBody()).id());
    }


    @Test
    void 구매자의_기본_정보가_올바르게_설정된다(@Autowired TestFixture fixture) {
        String email = generateEmail();
        String password = generatePassword();
        String username = generateUsername();

        fixture.createShopper(email, username, password);
        fixture.setShopperAsDefaultUser(email, password);


        ShopperMeView actual = fixture.client().getForObject(
                "/shopper/me",
                ShopperMeView.class
        );

        assertThat(actual.email()).isEqualTo(email);
        assertThat(actual.username()).isEqualTo(username);
    }
}

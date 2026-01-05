package wisoft.tddstart.commerce.api.seller.me;


import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static wisoft.tddstart.commerce.EmailGenerator.generateEmail;
import static wisoft.tddstart.commerce.PasswordGenerator.generatePassword;
import static wisoft.tddstart.commerce.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.commerce.api.TestFixture;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.command.CreateSellerCommand;
import wisoft.tddstart.commerce.query.IssueSellerToken;
import wisoft.tddstart.commerce.result.AccessTokenCarrier;
import wisoft.tddstart.commerce.view.SellerMeView;

@CommerceApiTest
@DisplayName("GET /seller/me")
public class GET_specs {

    @Test
    void 올바르게_요청하면_200_OK_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        //Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();


        var command = new CreateSellerCommand(email, username, password, generateEmail());
        client.postForEntity("/seller/signUp", command, Void.class);

        AccessTokenCarrier carries = client.postForObject(
                "/seller/issueToken", new IssueSellerToken(email, password),
                AccessTokenCarrier.class
        );

        String token = carries.accessToken();


        //Act
        ResponseEntity<SellerMeView> response = client.exchange(
                get("/seller/me")
                        .header("Authorization", "Bearer " + token)
                        .build(),
                SellerMeView.class
        );

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }


    @Test
    void 접근_토큰을_사용하지_않으면_401_Unauthorized_상태코드를_반환한다(@Autowired TestRestTemplate client) {

        //Arrange
        ResponseEntity<SellerMeView> response = client.getForEntity("/seller/me", SellerMeView.class);

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }


    @Test
    void 서로_다른_판매자의_식별자는_서로_다르다(@Autowired TestRestTemplate client) {

        String email1 = generateEmail();
        String username1 = generateUsername();
        String password1 = generatePassword();

        var command1 = new CreateSellerCommand(email1, username1, password1, generateEmail());
        client.postForEntity("/seller/signUp", command1, Void.class);

        AccessTokenCarrier carries1 = client.postForObject(
                "/seller/issueToken",
                new IssueSellerToken(email1, password1),
                AccessTokenCarrier.class
        );

        String token1 = carries1.accessToken();


        String email2 = generateEmail();
        String username2 = generateUsername();
        String password2 = generatePassword();

        var command2 = new CreateSellerCommand(email2, username2, password2, generateEmail());
        client.postForEntity("/seller/signUp", command2, Void.class);

        AccessTokenCarrier carries2 = client.postForObject("/seller/issueToken",
                new IssueSellerToken(email2, password2),
                AccessTokenCarrier.class);

        String token2 = carries2.accessToken();

        //Act
        ResponseEntity<SellerMeView> response1 =  client.exchange(
                get("/seller/me")
                        .header("Authorization", "Bearer " + token1)
                        .build(),
                SellerMeView.class
        );


        ResponseEntity<SellerMeView> response2 =  client.exchange(
                get("/seller/me")
                        .header("Authorization", "Bearer " + token2)
                        .build(),
                SellerMeView.class
        );


        //Assert
        assertThat(requireNonNull(response1.getBody()).id())
                .isNotEqualTo(requireNonNull(response2.getBody()).id());
    }


    @Test
    void 같은_판매자의_식별자는_항상_같다(@Autowired TestRestTemplate client) {
        //Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();

        var command = new CreateSellerCommand(email, username, password, generateEmail());
        client.postForEntity("/seller/signUp", command, Void.class);

        AccessTokenCarrier carrier1 = client.postForObject(
                "/seller/issueToken",
                new IssueSellerToken(email, password),
                AccessTokenCarrier.class);

        String token1= carrier1.accessToken();

        AccessTokenCarrier carrier2 = client.postForObject(
                "/seller/issueToken",
                new IssueSellerToken(email, password),
                AccessTokenCarrier.class);

        String token2 = carrier1.accessToken();

        ResponseEntity<SellerMeView> response1 = client.exchange(
                    get("/seller/me")
                            .header("Authorization", "Bearer " + token1)
                            .build(),
                SellerMeView.class
                );

        ResponseEntity<SellerMeView> response2 = client.exchange(
                get("/seller/me")
                        .header("Authorization", "Bearer " + token2)
                        .build(),
                SellerMeView.class
        );


        assertThat(requireNonNull(response1.getBody()).id())
                .isEqualTo(requireNonNull(response2.getBody()).id());
    }

    @Test
     void 판매자의_기본_정보가_올바르게_설정된다(@Autowired TestRestTemplate client) {
        //Arrange
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();

        var command = new CreateSellerCommand(email, username, password, generateEmail());
        client.postForEntity("/seller/signUp", command, Void.class);

        AccessTokenCarrier carrier = client.postForObject(
                "/seller/issueToken",
                new IssueSellerToken(email, password),
                AccessTokenCarrier.class);

        String token1= carrier.accessToken();

        ResponseEntity<SellerMeView> response = client.exchange(
                get("/seller/me")
                        .header("Authorization", "Bearer " + token1)
                        .build(),
                SellerMeView.class
        );

        SellerMeView actual = requireNonNull(response.getBody());
        assertThat(actual.email()).isEqualTo(email);
        assertThat(actual.username()).isEqualTo(username);
    }

    @Test
    void 문의_이메일_주소를_올바르게_설정한다(
            @Autowired TestFixture fixture
    ){
        String email = generateEmail();
        String username = generateUsername();
        String password = generatePassword();
        String contactEmail = generateEmail();

        fixture.createSeller(email, username, password, contactEmail);

        AccessTokenCarrier carrier =  fixture.client().postForObject(
                "/seller/issueToken",
                new IssueSellerToken(email, password),
                AccessTokenCarrier.class
        );
        String token = carrier.accessToken();

        ResponseEntity<SellerMeView> response = fixture.client().exchange(
                get("/seller/me")
                        .header("Authorization" , "Bearer " + token)
                        .build(),
                SellerMeView.class
        );

        SellerMeView actual = requireNonNull(response.getBody());
        assertThat(actual.contactEmail()).isEqualTo(contactEmail);
    }
}

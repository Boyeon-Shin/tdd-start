package wisoft.tddstart.commerce.api.seller.changecontactemail;

import static org.assertj.core.api.Assertions.assertThat;
import static wisoft.tddstart.commerce.EmailGenerator.generateEmail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import wisoft.tddstart.commerce.api.TestFixture;
import wisoft.tddstart.commerce.InvalidEmailSource;
import wisoft.tddstart.commerce.api.CommerceApiTest;
import wisoft.tddstart.commerce.command.ChangeContactEmailCommand;
import wisoft.tddstart.commerce.view.SellerMeView;

@CommerceApiTest
@DisplayName("POST /seller/changeContactEmail")
public class POST_specs {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        //Arrange
        fixture.createSellerThenSetAsDefaultUser();
        String contactEmail = generateEmail();

        //Act
        ResponseEntity<Void> response = fixture.client().postForEntity(
                "/seller/changeContactEmail",
                new ChangeContactEmailCommand(contactEmail),
                Void.class
        );

        //Assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @ParameterizedTest
    @InvalidEmailSource
    void contactEmail_속성이_올바르게_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String contactEmail,
            @Autowired TestFixture fixture
    ) {
        fixture.createSellerThenSetAsDefaultUser();

        ResponseEntity<Void> response = fixture.client().postForEntity(
                "/seller/changeContactEmail",
                new ChangeContactEmailCommand(contactEmail),
                Void.class
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void 문의_이메일_주소를_올바르게_변경한다(@Autowired TestFixture fixture) {
        fixture.createSellerThenSetAsDefaultUser();
        String contactEmail = generateEmail();

        ResponseEntity<Void> response = fixture.client().postForEntity(
                "/seller/changeContactEmail",
                new ChangeContactEmailCommand(contactEmail),
                Void.class
        );

        SellerMeView seller = fixture.getSeller();
        assertThat(seller.contactEmail()).isEqualTo(contactEmail);
    }
}
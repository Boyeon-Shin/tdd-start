package wisoft.tddstart;

import static wisoft.tddstart.EmailGenerator.generateEmail;
import static wisoft.tddstart.PasswordGenerator.generatePassword;
import static wisoft.tddstart.UsernameGenerator.generateUsername;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestTemplate;
import wisoft.tddstart.commerce.command.CreateShopperCommand;
import wisoft.tddstart.commerce.query.IssueShopperToken;
import wisoft.tddstart.commerce.result.AccessTokenCarrier;

public record TestFixture(TestRestTemplate client) {
    public void createShopper(final String email, final String username, final String password) {
        var command = new CreateShopperCommand(email, username, password);
        client.postForEntity("/shopper/signUp", command, Void.class);
    }

    public String issueShopperToken(final String email, final String password) {
            AccessTokenCarrier carrier = client().postForObject(
                    "/shopper/issueToken",
                    new IssueShopperToken(email, password),
                    AccessTokenCarrier.class
            );

        return carrier.accessToken();
    }

    public String createShopperThenIssueToken() {
        String email = generateEmail();
        String password = generatePassword();
        createShopper(email, generateUsername(), password);
        return issueShopperToken(email, password);
    }

    public void setShopperAsDefaultUser(final String email, final String password) {
        String token = issueShopperToken(email, password);
        RestTemplate template = client.getRestTemplate();

        template.getInterceptors().add((request, body, execution) ->  {
            if (!request.getHeaders().containsKey("Authorization")) {
                request.getHeaders().add("Authorization", "Bearer " + token);
            }
            return execution.execute(request, body);
        });
    }
}

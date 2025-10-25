package wisoft.tddstart;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

public class TestFixtureConfiguration {

    @Bean
    public TestFixture testFixture(TestRestTemplate client) {
        return new TestFixture(client);
    }
}

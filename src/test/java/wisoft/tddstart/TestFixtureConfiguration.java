package wisoft.tddstart;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import wisoft.tddstart.commerce.ProductRepository;

public class TestFixtureConfiguration {

    @Bean
    @Scope("prototype")
    public TestFixture testFixture(Environment environment, ProductRepository productRepository) {
        return TestFixture.create(environment, productRepository);
    }

//    @Bean
//    public TestFixture testFixture(TestRestTemplate client) {
//        return new TestFixture(client);
//    }
}

package wisoft.tddstart.commerce.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import wisoft.tddstart.TddStartApplication;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
        classes = TddStartApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public @interface CommerceApiTest {


}

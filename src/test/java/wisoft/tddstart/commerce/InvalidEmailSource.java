package wisoft.tddstart.commerce;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import org.junit.jupiter.params.provider.MethodSource;

@Retention(RUNTIME)
@MethodSource("wisoft.tddstart.commerce.TestDataSource#invalidEmails")
public @interface InvalidEmailSource {
}

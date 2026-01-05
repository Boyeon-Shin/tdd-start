package wisoft.tddstart;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import org.junit.jupiter.params.provider.MethodSource;

@Retention(RUNTIME)
@MethodSource("wisoft.tddstart.TestDataSource#invalidEmails")
public @interface InvalidEmailSource {
}

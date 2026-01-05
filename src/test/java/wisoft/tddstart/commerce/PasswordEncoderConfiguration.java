package wisoft.tddstart.commerce;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;

public class PasswordEncoderConfiguration {

    public static final int SALT_LENGTH = 16;
    public static final int ITERATIONS = 10;

    @Bean
    @Primary
    Pbkdf2PasswordEncoder testPasswordEncoder() {
        return new Pbkdf2PasswordEncoder(
                "",
                SALT_LENGTH,
                ITERATIONS,
                SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256
        );
    }
}

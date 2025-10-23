package wisoft.tddstart;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.assertj.core.api.ThrowingConsumer;

public class JwtAssertion {
    public static ThrowingConsumer<String> conformsToJwtFormat() {
        return s -> {
            String[] parts = s.split("\\.");
            assertThat(parts).hasSize(3);

            assertThat(parts[0]).matches(JwtAssertion::isBase64EncodedJson);
            assertThat(parts[1]).matches(JwtAssertion::isBase64EncodedJson);
            assertThat(parts[2]).matches(JwtAssertion::isBased64UrlEncoded);
        };
    }

    private static boolean isBase64EncodedJson(final String s) {
        try {
            new ObjectMapper().readTree(Base64.getUrlDecoder().decode(s));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isBased64UrlEncoded(String s) {
        try {
            Base64.getUrlDecoder().decode(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

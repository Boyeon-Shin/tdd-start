package wisoft.tddstart;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.math.BigDecimal;
import java.util.function.Predicate;
import org.assertj.core.api.ThrowingConsumer;
import wisoft.tddstart.commerce.command.RegisterProductCommand;
import wisoft.tddstart.commerce.view.ProductView;
import wisoft.tddstart.commerce.view.SellerProductView;

public class ProductAssertions {
    public static ThrowingConsumer<? super SellerProductView> isDerivedForm(
            final RegisterProductCommand command
    ) {

        return product -> {
            assertThat(product.description()).isEqualTo(command.description());
            assertThat(product.imageUri()).isEqualTo(command.imageUri());
            assertThat(product.priceAmount()).matches(equals(command.priceAmount()));
            assertThat(product.stockQuantity()).isEqualTo(command.stockQuantity());
        };
    }

    private static Predicate<? super BigDecimal> equals(BigDecimal expected) {
        return actual -> actual.compareTo(expected) == 0;
    }

    public static  ThrowingConsumer<? super ProductView> isViewDerivedFrom(final RegisterProductCommand command) {
        return product -> {
            assertThat(product.name()).isEqualTo(command.name());
            assertThat(product.description()).isEqualTo(command.description());
            assertThat(product.imageUri()).isEqualTo(command.imageUri());
            assertThat(product.priceAmount()).matches(equals(command.priceAmount()));
            assertThat(product.stockQuantity()).isEqualTo(command.stockQuantity());
        };
    }
}

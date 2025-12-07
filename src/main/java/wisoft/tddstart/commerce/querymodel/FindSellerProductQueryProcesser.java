package wisoft.tddstart.commerce.querymodel;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import wisoft.tddstart.commerce.Product;

public class FindSellerProductQueryProcesser {

    public final Function<UUID, Optional<Product>> findProduct;

    public FindSellerProductQueryProcesser(Function<UUID, Optional<Product>> findProduct) {
        this.findProduct = findProduct;
    }
}

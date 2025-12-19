package wisoft.tddstart.commerce.querymodel;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.query.FindSellerProduct;
import wisoft.tddstart.commerce.view.SellerProductView;

public class FindSellerProductQueryProcesser {

    private final Function<UUID, Optional<Product>> findProduct;

    public FindSellerProductQueryProcesser(Function<UUID, Optional<Product>> findProduct) {
        this.findProduct = findProduct;
    }

    public Optional<SellerProductView> process(
            final FindSellerProduct query
    ) {
        return findProduct
                .apply(query.productId())
                .filter(product -> product.getSellerId().equals(query.sellerId()))
                .map(ProductMapper::convertToView);
    }
}

package wisoft.tddstart.commerce.querymodel;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.query.GetSellerProducts;
import wisoft.tddstart.commerce.result.ArrayCarrier;
import wisoft.tddstart.commerce.view.SellerProductView;

public class GetSellerProductsQueryProcessor {

    private final Function<UUID, List<Product>> getProductsOfSeller;

    public GetSellerProductsQueryProcessor(Function<UUID, List<Product>> getProductsOfSeller) {
        this.getProductsOfSeller = getProductsOfSeller;
    }

    public ArrayCarrier<SellerProductView> process(GetSellerProducts query) {
        SellerProductView[] items = getProductsOfSeller.apply(query.sellerId())
                .stream()
                .sorted(Comparator.comparing(Product::getRegisteredTimeUtc, Comparator.reverseOrder()))
                .map(ProductMapper::convertToView)

                .toArray(SellerProductView[]::new);

        return new ArrayCarrier<>(items);
    }
}

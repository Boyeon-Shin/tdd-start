package wisoft.tddstart.commerce.querymodel;

import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.view.SellerProductView;

public class ProductMapper {

    public static SellerProductView convertToView(final Product product) {
        return new SellerProductView(
                product.getId(),
                product.getName(),
                product.getImageUri(),
                product.getDescription(),
                product.getPriceAmount(),
                product.getStockQuantity(),
                product.getRegisteredTimeUtc()
        );
    }
}

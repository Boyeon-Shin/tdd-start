package wisoft.tddstart.commerce.api.controller;

import static java.util.Comparator.reverseOrder;

import java.util.Comparator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.ProductRepository;
import wisoft.tddstart.commerce.result.PageCarrier;
import wisoft.tddstart.commerce.view.ProductView;

@RestController
public record ShopperProductController(ProductRepository repository) {

    @GetMapping("/shopper/products")
    PageCarrier<ProductView> getProducts() {
        ProductView[] items = repository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Product::getDataKey, reverseOrder()))
                .map(product -> new ProductView(
                        product.getId(),
                        null,
                        product.getName(),
                        product.getImageUri(),
                        product.getDescription(),
                        product.getPriceAmount(),
                        product.getStockQuantity()
                ))
                .toArray(ProductView[]::new);

        return new PageCarrier<>(items, null);
    }
}

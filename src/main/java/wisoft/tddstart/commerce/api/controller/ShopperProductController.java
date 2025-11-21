package wisoft.tddstart.commerce.api.controller;

import jakarta.persistence.EntityManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.result.PageCarrier;
import wisoft.tddstart.commerce.view.ProductView;
import wisoft.tddstart.commerce.view.SellerView;

@RestController
public record ShopperProductController(EntityManager entityManager) {

    @GetMapping("/shopper/products")
    PageCarrier<ProductView> getProducts() {
        String query = """
                SELECT new wisoft.tddstart.commerce.api.controller.ProductSellerTuple(p, s)
                FROM Product p
                JOIN Seller s ON p.sellerId = s.id
                ORDER BY p.dataKey DESC
                """;

        ProductView[] items = entityManager
                .createQuery(query, ProductSellerTuple.class)
                .getResultList()
                .stream()
                .map(tuple -> tuple.toView(tuple.product(), new SellerView(
                        tuple.seller().getId(),
                        tuple.seller().getUsername()
                )))
                .toArray(ProductView[]::new);

        return new PageCarrier<>(items, null);
    }
}

package wisoft.tddstart.commerce.api.controller;

import static io.jsonwebtoken.lang.Strings.UTF_8;

import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.result.PageCarrier;
import wisoft.tddstart.commerce.view.ProductView;
import wisoft.tddstart.commerce.view.SellerView;

@RestController
public record ShopperProductController(EntityManager entityManager) {

    @GetMapping("/shopper/products")
    PageCarrier<ProductView> getProducts(@RequestParam(required = false) String continuationToken) {
        String queryString = """
                SELECT new wisoft.tddstart.commerce.api.controller.ProductSellerTuple(p, s)
                FROM Product p
                JOIN Seller s ON p.sellerId = s.id
                WHERE :cursor IS NULL OR p.dataKey <= :cursor
                ORDER BY p.dataKey DESC
            """;

        int pageSize = 10;

        List<ProductSellerTuple> results = entityManager
                .createQuery(queryString, ProductSellerTuple.class)
                .setParameter("cursor", decodeCursor(continuationToken))
                .setMaxResults(pageSize + 1)
                .getResultList();

//        ProductView[] items = results
//                .stream()
//                .limit(pageSize)
//                .map(tuple -> tuple.toView(tuple.product(), new SellerView(
//                        tuple.seller().getId(),
//                        tuple.seller().getUsername()
//                )))
//                .toArray(ProductView[]::new);

        ProductView[] items = results
                .stream()
                .limit(pageSize)
                .map(ProductSellerTuple::toView)
                .toArray(ProductView[]::new);

        Long next = results.getLast().product().getDataKey();

        return new PageCarrier<>(items, encodeCursor(next));
    }

    private Long decodeCursor(String continuationToken) {
        if(continuationToken == null) {
            return null;
        }

        byte[] data = Base64.getDecoder().decode(continuationToken);
        return Long.parseLong(new String(data, UTF_8));
    }

    private String encodeCursor(Long cursor) {
        byte[] data = cursor.toString().getBytes(UTF_8);
        return Base64.getEncoder().encodeToString(data);
    }
}
package wisoft.tddstart.commerce.querymodel;

import static io.jsonwebtoken.lang.Strings.UTF_8;

import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.List;
import wisoft.tddstart.commerce.query.GetProductPage;
import wisoft.tddstart.commerce.result.PageCarrier;
import wisoft.tddstart.commerce.view.ProductView;

public class GetProductPageQueryProcessor {

    private final EntityManager entityManager;

    public GetProductPageQueryProcessor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PageCarrier<ProductView> process(GetProductPage query) {
        String queryString = """
                    SELECT new wisoft.tddstart.commerce.querymodel.ProductSellerTuple(p, s)
                    FROM Product p
                    JOIN Seller s ON p.sellerId = s.id
                    WHERE :cursor IS NULL OR p.dataKey <= :cursor
                    ORDER BY p.dataKey DESC
                """;

        int pageSize = 10;

        List<ProductSellerTuple> results = entityManager
                .createQuery(queryString, ProductSellerTuple.class)
                .setParameter("cursor", decodeCursor(query.continuationToken()))
                .setMaxResults(pageSize + 1)
                .getResultList();

        ProductView[] items = results
                .stream()
                .limit(pageSize)
                .map(ProductSellerTuple::toView)
                .toArray(ProductView[]::new);

        Long next = results.size() <= pageSize
                ? null
                : results.getLast().product().getDataKey();

        return new PageCarrier<>(items, encodeCursor(next));
    }

    private static Long decodeCursor(String continuationToken) {
        if (continuationToken == null || continuationToken.isBlank()) {
            return null;
        }

        byte[] data = Base64.getDecoder().decode(continuationToken);
        return Long.parseLong(new String(data, UTF_8));
    }

    private static String encodeCursor(Long cursor) {
        if (cursor == null) {
            return null;
        }

        byte[] data = cursor.toString().getBytes(UTF_8);
        return Base64.getEncoder().encodeToString(data);
    }
}

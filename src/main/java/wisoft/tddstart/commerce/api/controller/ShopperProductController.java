package wisoft.tddstart.commerce.api.controller;

import jakarta.persistence.EntityManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.query.GetProductPage;
import wisoft.tddstart.commerce.querymodel.GetProductPageQueryProcessor;
import wisoft.tddstart.commerce.result.PageCarrier;
import wisoft.tddstart.commerce.view.ProductView;

@RestController
public record ShopperProductController(EntityManager entityManager) {

    @GetMapping("/shopper/products")
    PageCarrier<ProductView> getProducts(@RequestParam(required = false) String continuationToken) {
        var processor = new GetProductPageQueryProcessor(entityManager);
        var query = new GetProductPage(continuationToken);
        return processor.process(query);
    }
}
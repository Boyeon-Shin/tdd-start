package wisoft.tddstart.commerce.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopperProductController {

    @GetMapping("/shopper/products")
    void getProducts() {
    }
}

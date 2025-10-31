package wisoft.tddstart.commerce.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SellerProductsController {

    @PostMapping("/seller/products")
    ResponseEntity<?> registerProduct() {
        return ResponseEntity.status(201).build();
    }
}

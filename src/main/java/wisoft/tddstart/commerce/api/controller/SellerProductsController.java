package wisoft.tddstart.commerce.api.controller;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.ProductRepository;
import wisoft.tddstart.commerce.command.RegisterProductCommand;
import wisoft.tddstart.commerce.view.SellerProductView;

@RestController
public record SellerProductsController(ProductRepository repository) {
    @PostMapping("/seller/products")
    ResponseEntity<?> registerProduct(
            @RequestBody RegisterProductCommand command,
            Principal user

    ) {
        if(isValidUri(command.imageUri()) == false) {
            return ResponseEntity.badRequest().build();
        }


        UUID id = UUID.randomUUID();
        var product = new Product();
        product.setId(id);
        product.setSellerId(UUID.fromString(user.getName()));
        System.out.println("setName" + command.name());
        product.setName(command.name());
        product.setImageUri(command.imageUri());
        product.setDescription(command.description());
        product.setPriceAmount(command.priceAmount());
        product.setStockQuantity(command.stockQuantity());
        repository.save(product);


        URI location = URI.create("/seller/products/" + id);
        return ResponseEntity.created(location).build();
    }

    private boolean isValidUri(final String value) {
        try {
            URI uri =  URI.create(value);
            return uri.getHost() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @GetMapping("/seller/products/{id}")
    ResponseEntity<?> findProduct(@PathVariable UUID id, Principal user) {
        UUID sellerId = UUID.fromString(user.getName());
        System.out.println("sellerId " + sellerId);

        return repository
                .findById(id)
                .filter(product -> product.getSellerId().equals(sellerId))
                .map(product -> new SellerProductView(
                        product.getId(),
                        product.getName(),
                        product.getImageUri(),
                        product.getDescription(),
                        product.getPriceAmount(),
                        product.getStockQuantity(),
                        null
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
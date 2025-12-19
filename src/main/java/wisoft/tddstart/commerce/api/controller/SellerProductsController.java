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
import wisoft.tddstart.commerce.ProductRepository;
import wisoft.tddstart.commerce.command.RegisterProductCommand;
import wisoft.tddstart.commerce.commandModel.RegisterProductCommandExecutor;
import wisoft.tddstart.commerce.query.FindSellerProduct;
import wisoft.tddstart.commerce.query.GetSellerProducts;
import wisoft.tddstart.commerce.querymodel.FindSellerProductQueryProcesser;
import wisoft.tddstart.commerce.querymodel.GetSellerProductsQueryProcessor;
import wisoft.tddstart.commerce.result.ArrayCarrier;
import wisoft.tddstart.commerce.view.SellerProductView;


@RestController
public record SellerProductsController(ProductRepository repository) {

    @PostMapping("/seller/products")
    ResponseEntity<?> registerProduct(
            @RequestBody RegisterProductCommand command,
            Principal user

    ) {
        UUID id = UUID.randomUUID();
        var executor = new RegisterProductCommandExecutor(repository::save);
        executor.execute(id, UUID.fromString(user.getName()), command);
        URI location = URI.create("/seller/products/" + id);
        return ResponseEntity.created(location).build();

    }

    @GetMapping("/seller/products/{id}")
    ResponseEntity<?> findProduct(@PathVariable UUID id, Principal user) {
        var processor = new FindSellerProductQueryProcesser(repository::findById);
        var query = new FindSellerProduct(UUID.fromString(user.getName()), id);

        return ResponseEntity.of(processor.process(query));
    }

//    @GetMapping("/seller/products")
//    ResponseEntity<?> getProducts(Principal user) {
//        UUID sellerId = UUID.fromString(user.getName());
//        SellerProductView[] items = repository
//                .findBySellerId(sellerId)
//                .stream()
//                .sorted(Comparator.comparing(Product::getRegisteredTimeUtc, Comparator.reverseOrder()))
//                .map(ProductMapper::convertToView)
//                .toArray(SellerProductView[]::new);
//
//        return ResponseEntity.ok(new ArrayCarrier<>(items));
//    }

    @GetMapping("/seller/products")
    ArrayCarrier<SellerProductView> getProducts(Principal user) {
        var processor = new GetSellerProductsQueryProcessor(repository::findBySellerId);
        var query = new GetSellerProducts(UUID.fromString(user.getName()));

        return processor.process(query);
    }
}
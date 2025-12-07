package wisoft.tddstart.commerce.api.controller;

import static wisoft.tddstart.commerce.querymodel.ProductMapper.convertToView;

import java.net.URI;
import java.security.Principal;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.ProductRepository;
import wisoft.tddstart.commerce.command.RegisterProductCommand;
import wisoft.tddstart.commerce.commandModel.RegisterProductCommandExecutor;
import wisoft.tddstart.commerce.query.FindSellerProduct;
import wisoft.tddstart.commerce.querymodel.FindSellerProductQueryProcesser;
import wisoft.tddstart.commerce.querymodel.ProductMapper;
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
        UUID sellerId = UUID.fromString(user.getName());
        System.out.println("sellerId " + sellerId);

        Function<UUID, Optional<Product>> findProduct = repository::findById;
        var processor = new FindSellerProductQueryProcesser(findProduct);
        var query = new FindSellerProduct(sellerId, id);

        return process(processor, query)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static Optional<SellerProductView> process(
            final FindSellerProductQueryProcesser processor,
            final FindSellerProduct query
    ) {
        return processor.findProduct.apply(query.productId())
                .filter(product -> product.getSellerId().equals(query.sellerId()))
                .map(ProductMapper::convertToView);
    }

    //.sorted(Comparator.comparing(Product::getRegisteredTimeUtc, Comparator.reverseOrder()))  이걸 넣지 않아도 통과되는 이유?
    @GetMapping("/seller/products")
    ResponseEntity<?> getProducts(Principal user) {
        UUID sellerId = UUID.fromString(user.getName());

        SellerProductView[] items = repository
                .findBySellerId(sellerId)
                .stream()
                .sorted(Comparator.comparing(Product::getRegisteredTimeUtc, Comparator.reverseOrder()))
                .map(ProductMapper::convertToView)
                .toArray(SellerProductView[]::new);

        return ResponseEntity.ok(new ArrayCarrier<>(items));
    }
}
package wisoft.tddstart.commerce.commandModel;

import static java.time.ZoneOffset.UTC;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;
import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.command.RegisterProductCommand;

public class RegisterProductCommandExecutor {

    private final Consumer<Product> saveProduct;

    public RegisterProductCommandExecutor(Consumer<Product> saveProduct) {
        this.saveProduct = saveProduct;
    }

    public void execute(
            final UUID productId,
            final UUID sellerId,
            final RegisterProductCommand command) {

        validateCommand(command);
        var product = createProduct(productId, sellerId, command);
        saveProduct(product);
    }

    private static void validateCommand(final RegisterProductCommand command) {
        if (isValidUri(command.imageUri()) == false) {
            throw new InvalidCommandException();
        }
    }

    private void saveProduct(final Product product) {
        saveProduct.accept(product);
    }

    private static Product createProduct(final UUID productId, final UUID sellerId, final RegisterProductCommand command) {
        var product = new Product();
        product.setId(productId);
        product.setSellerId(sellerId);
        System.out.println("setName" + command.name());
        product.setName(command.name());
        product.setImageUri(command.imageUri());
        product.setDescription(command.description());
        product.setPriceAmount(command.priceAmount());
        product.setStockQuantity(command.stockQuantity());
        product.setRegisteredTimeUtc(LocalDateTime.now(UTC));
        return product;
    }

    private static boolean isValidUri(final String value) {
        try {
            URI uri = URI.create(value);
            return uri.getHost() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


}

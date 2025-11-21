package wisoft.tddstart.commerce.api.controller;

import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.Seller;
import wisoft.tddstart.commerce.view.ProductView;
import wisoft.tddstart.commerce.view.SellerView;

record ProductSellerTuple(Product product, Seller seller) {

    ProductView toView(final Product tuple, final SellerView tuple1) {
        return new ProductView(
                tuple.getId(),
                tuple1,
                tuple.getName(),
                tuple.getImageUri(),
                tuple.getDescription(),
                tuple.getPriceAmount(),
                tuple.getStockQuantity()
        );
    }
}

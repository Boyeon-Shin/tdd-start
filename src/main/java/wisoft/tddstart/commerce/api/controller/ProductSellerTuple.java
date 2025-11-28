package wisoft.tddstart.commerce.api.controller;

import wisoft.tddstart.commerce.Product;
import wisoft.tddstart.commerce.Seller;
import wisoft.tddstart.commerce.view.ProductView;
import wisoft.tddstart.commerce.view.SellerView;

record ProductSellerTuple(Product product, Seller seller) {

    ProductView toView() {
        return new ProductView(
                product.getId(),
                new SellerView(seller().getId(), seller().getUsername()),
                product.getName(),
                product.getImageUri(),
                product.getDescription(),
                product.getPriceAmount(),
                product.getStockQuantity()
        );
    }
}

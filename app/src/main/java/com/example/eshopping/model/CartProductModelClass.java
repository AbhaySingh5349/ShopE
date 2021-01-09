package com.example.eshopping.model;

public class CartProductModelClass {

    String ProductDate, ProductDescription, ProductId, ProductName, ProductPrice, ProductPublisherId, StockQuantity, DefaultCartQuantity, CartItemId, BuyerId;

    public CartProductModelClass(String productDate, String productDescription, String productId, String productName, String productPrice, String productPublisherId, String stockQuantity, String defaultCartQuantity, String cartItemId, String buyerId) {
        ProductDate = productDate;
        ProductDescription = productDescription;
        ProductId = productId;
        ProductName = productName;
        ProductPrice = productPrice;
        ProductPublisherId = productPublisherId;
        StockQuantity = stockQuantity;
        DefaultCartQuantity = defaultCartQuantity;
        CartItemId = cartItemId;
        BuyerId = buyerId;
    }

    public String getProductDate() {
        return ProductDate;
    }

    public void setProductDate(String productDate) {
        ProductDate = productDate;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductPublisherId() {
        return ProductPublisherId;
    }

    public void setProductPublisherId(String productPublisherId) {
        ProductPublisherId = productPublisherId;
    }

    public String getStockQuantity() {
        return StockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        StockQuantity = stockQuantity;
    }

    public String getDefaultCartQuantity() {
        return DefaultCartQuantity;
    }

    public void setDefaultCartQuantity(String defaultCartQuantity) {
        DefaultCartQuantity = defaultCartQuantity;
    }

    public String getCartItemId() {
        return CartItemId;
    }

    public void setCartItemId(String cartItemId) {
        CartItemId = cartItemId;
    }

    public String getBuyerId() {
        return BuyerId;
    }

    public void setBuyerId(String buyerId) {
        BuyerId = buyerId;
    }

    public CartProductModelClass() {
    }
}

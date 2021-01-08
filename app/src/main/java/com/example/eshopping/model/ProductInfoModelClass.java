package com.example.eshopping.model;

public class ProductInfoModelClass {

    String ProductDate, ProductDescription, ProductId, ProductName, ProductPrice, ProductPublisherId, ProductQuantity;

    public ProductInfoModelClass(String productDate, String productDescription, String productId, String productName, String productPrice, String productPublisherId, String productQuantity) {
        ProductDate = productDate;
        ProductDescription = productDescription;
        ProductId = productId;
        ProductName = productName;
        ProductPrice = productPrice;
        ProductPublisherId = productPublisherId;
        ProductQuantity = productQuantity;
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

    public String getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        ProductQuantity = productQuantity;
    }

    public ProductInfoModelClass() {
    }
}

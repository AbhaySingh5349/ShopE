package com.example.eshopping.model;

import java.util.ArrayList;

public class OrdersModelClass {

    String PayableAmount, BuyerId, OrderId, Address, PlaceName, Contact;
    ArrayList<String> CartItemIds;

    public OrdersModelClass(String payableAmount, String buyerId, String orderId, String address, String placeName, String contact, ArrayList<String> cartItemIds) {
        PayableAmount = payableAmount;
        BuyerId = buyerId;
        OrderId = orderId;
        Address = address;
        PlaceName = placeName;
        Contact = contact;
        CartItemIds = cartItemIds;
    }

    public String getPayableAmount() {
        return PayableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        PayableAmount = payableAmount;
    }

    public String getBuyerId() {
        return BuyerId;
    }

    public void setBuyerId(String buyerId) {
        BuyerId = buyerId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public ArrayList<String> getCartItemIds() {
        return CartItemIds;
    }

    public void setCartItemIds(ArrayList<String> cartItemIds) {
        CartItemIds = cartItemIds;
    }

    public OrdersModelClass() {
    }
}

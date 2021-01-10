package com.example.eshopping.model;

public class DeliveryAddressModelClass {

    String Address, Contact, PlaceName, AddressId, UserId;
    Double Latitude, Longitude;

    public DeliveryAddressModelClass(String address, String contact, String placeName, String addressId, String userId, Double latitude, Double longitude) {
        Address = address;
        Contact = contact;
        PlaceName = placeName;
        AddressId = addressId;
        UserId = userId;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getAddressId() {
        return AddressId;
    }

    public void setAddressId(String addressId) {
        AddressId = addressId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public DeliveryAddressModelClass() {
    }
}

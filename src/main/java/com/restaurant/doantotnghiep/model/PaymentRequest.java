package com.restaurant.doantotnghiep.model;

public class PaymentRequest {

    private long amount;
    private String description;

    private String name;
    private int quantity;
    private long price;

    private String returnUrl;
    private String cancelUrl;

    // ---------- GETTERS & SETTERS ----------
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) { // FIX: đổi int → long
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
}

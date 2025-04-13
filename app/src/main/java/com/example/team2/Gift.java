package com.example.team2;

public class Gift {
    private String name;
    private double price;
    private String imageUrl;
    private int quantity;

    public Gift(String name, double price, String imageUrl) {
        this(name, price, imageUrl, 0);
    }

    public Gift(String name, double price, String imageUrl, int quantity) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
package com.example.team2;

import com.google.gson.annotations.SerializedName;

public class Gift {
    @SerializedName("title")
    private String name;
    private double price;
    @SerializedName("url")
    private String imageUrl;
    private int quantity = 0;

    public Gift(String name, double price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
package com.example.team2;

public class Category {
    private String name;
    private int colorResId;

    public Category(String name, int colorResId) {
        this.name = name;
        this.colorResId = colorResId;
    }

    public String getName() {
        return name;
    }

    public int getColorResId() {
        return colorResId;
    }
}
package com.nathafw.demo.model;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private boolean available;

    public Product() {}

    public Product(int id, String name, String category, double price, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public boolean available() {
        return available;
    }

    public int id() {
        return getId();
    }

    public String name() {
        return getName();
    }

    public String category() {
        return getCategory();
    }

    public double price() {
        return getPrice();
    }
}
package com.nathafw.demo.service;

import java.util.ArrayList;
import java.util.List;
import com.nathafw.demo.model.Product;

public class ProductService {
    private static ProductService instance;
    private List<Product> products;

    private ProductService() {
        products = new ArrayList<>(List.of(
                new Product(1, "Clavier mécanique", "Informatique", 185_000, true),
                new Product(2, "Souris sans fil", "Informatique", 95_000, true),
                new Product(3, "Casque audio", "Audio", 140_000, false)));
    }

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    public List<Product> findAll() {
        return products;
    }

    public Product findFeatured() {
        return findAll().get(0);
    }

    public Product findById(int id) {
        return findAll().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void create(Product product) {
        products.add(product);
    }
}

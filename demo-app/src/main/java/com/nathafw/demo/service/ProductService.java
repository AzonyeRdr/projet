package com.nathafw.demo.service;

import java.util.List;
import com.nathafw.demo.model.Product;

public class ProductService {
    public List<Product> findAll() {
        return List.of(
                new Product(1, "Clavier mécanique", "Informatique", 185_000, true),
                new Product(2, "Souris sans fil", "Informatique", 95_000, true),
                new Product(3, "Casque audio", "Audio", 140_000, false));
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
}

package com.nathafw.demo.controller;

import com.nathafw.demo.model.Product;
import com.nathafw.demo.service.ProductService;
import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.URLAnnotation;
import mg.nathafw.mapping.HTTPMethod;
import mg.nathafw.util.ModelView;

@MyController
public class ProductController {
    private final ProductService productService = new ProductService();

    @URLAnnotation(value = "/products", httpMethod = HTTPMethod.GET)
    public ModelView list() {
        return new ModelView("products/list.jsp")
                .add("title", "Catalogue de démonstration")
                .add("products", productService.findAll());
    }

    @URLAnnotation(value = "/products/featured", httpMethod = HTTPMethod.GET)
    public ModelView featured() {
        Product product = productService.findFeatured();
        return new ModelView("products/detail.jsp")
                .add("product", product)
                .add("statusLabel", product.available() ? "Disponible" : "Indisponible");
    }

    @URLAnnotation(value = "/products/create", httpMethod = HTTPMethod.POST)
    public ModelView create() {
        return new ModelView("status/success.jsp")
                .add("title", "Route POST exécutée")
                .add("message", "Le framework a correctement distingué POST /products/create.");
    }
}

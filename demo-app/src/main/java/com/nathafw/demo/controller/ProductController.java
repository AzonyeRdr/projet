package com.nathafw.demo.controller;

import com.nathafw.demo.model.Product;
import com.nathafw.demo.service.ProductService;
import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.Param;
import mg.nathafw.annotation.URLAnnotation;
import mg.nathafw.mapping.HTTPMethod;
import mg.nathafw.util.ModelView;

@MyController
public class ProductController {
    private final ProductService productService = ProductService.getInstance();

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

    @URLAnnotation(value = "/products/create", httpMethod = HTTPMethod.GET)
    public ModelView create() {
        return new ModelView("products/create.jsp");
    }


    @URLAnnotation(value = "/products/create", httpMethod = HTTPMethod.POST)
    public ModelView create(@Param("produit") Product product) {
        productService.create(product);

        return list();
    }

    @URLAnnotation(value = "/test", httpMethod = HTTPMethod.GET)
    public ModelView findByid(@Param("id") int id) {
        Product product = productService.findById(id);
        if (product == null) {
            return new ModelView("status/error.jsp")
                    .add("title", "Produit non trouvé")
                    .add("message", "Le produit avec l'ID " + id + " n'a pas été trouvé.");
        }
        return new ModelView("products/test.jsp")
                .add("product", product)
                .add("statusLabel", product.available() ? "Disponible" : "Indisponible");
    }
}
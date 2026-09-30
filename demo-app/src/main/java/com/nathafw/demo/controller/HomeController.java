package com.nathafw.demo.controller;

import mg.nathafw.annotation.MyController;
import mg.nathafw.annotation.URLAnnotation;
import mg.nathafw.mapping.HTTPMethod;
import mg.nathafw.util.ModelView;

@MyController
public class HomeController {
    @URLAnnotation(value = "/", httpMethod = HTTPMethod.GET)
    public ModelView home() {
        return new ModelView("home.jsp")
                .add("applicationName", "Application de test NathaFw")
                .add("message", "Le FrontController, le scan et le rendu JSP fonctionnent.");
    }

    @URLAnnotation(value = "/about", httpMethod = HTTPMethod.GET)
    public ModelView aboutHtml() {
        return new ModelView("about.jsp");
    }

    @URLAnnotation(value = "/debug", httpMethod = HTTPMethod.GET)
    public String debug() {
        return "Cette valeur n'est pas un ModelView";
    }
}

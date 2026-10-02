package com.nathafw.demo.controller;

import mg.nathafw.annotation.MyController;

import com.nathafw.demo.model.Test;

import mg.nathafw.annotation.APIAnnotation;
import mg.nathafw.mapping.HTTPMethod;

@MyController
public class TestApiController {

    @APIAnnotation(value = "/api/hello", httpMethod = HTTPMethod.GET)
    public String hello() {
        return "Hello from demo-app API";
    }

    @APIAnnotation(value = "/api/test",httpMethod = HTTPMethod.GET)
    public Test test(){
        return new Test(2,"Alice");
    }

    @APIAnnotation(value = "/api/sum", httpMethod = HTTPMethod.POST)
    public String sum() {
        return "3";
    }
}

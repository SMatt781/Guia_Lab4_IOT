package com.example.directorioempleados;

import java.util.List;

// DummyJSON proporciona usuarios ficticios con datos de empresa.
// Aquí solo se mapean los campos necesarios para un directorio de práctica.
public final class ApiModels {
    public static class UsersResponse { public List<Employee> users; public int total; }
    public static class Company { public String name, department, title; }
    public static class Address { public String address, city, state; }
    public static class Employee {
        public int id, age;
        public String firstName, lastName, email, phone, image;
        public Company company;
        public Address address;
    }
}

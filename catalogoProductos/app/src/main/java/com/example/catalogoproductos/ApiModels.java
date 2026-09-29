package com.example.catalogoproductos;

import java.util.List;

public final class ApiModels {
    public static class Category { public String slug, name, url; }
    public static class ProductsResponse { public List<Product> products; public int total; }
    public static class Product {
        public int id, stock;
        public String title, description, category, brand, thumbnail;
        public double price, rating, discountPercentage;
    }
}

package com.example.catalogoproductos;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

// JSON mínimo de prueba: no realiza conexiones ni usa datos reales de personas.
public class ApiModelsTest {
    @Test public void parsesDomainJson() {
        ApiModels.ProductsResponse response = new Gson().fromJson("{\"products\":[{\"id\":1,\"title\":\"Phone\",\"price\":99.5,\"stock\":3}]}", ApiModels.ProductsResponse.class);
        assertEquals(1, response.products.size());
        assertEquals("Phone", response.products.get(0).title);
        assertEquals(99.5, response.products.get(0).price, 0.001);
        ApiModels.Category category = new Gson().fromJson("{\"slug\":\"smartphones\",\"name\":\"Smartphones\"}", ApiModels.Category.class);
        assertEquals("smartphones", category.slug);
    }

    @Test public void emptyResponsesAreRepresentable() {
        ApiModels.ProductsResponse empty = new Gson().fromJson("{\"products\":[]}", ApiModels.ProductsResponse.class);
        assertTrue(empty.products.isEmpty());
    }
}


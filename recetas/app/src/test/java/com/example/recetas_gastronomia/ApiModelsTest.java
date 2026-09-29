package com.example.recetas_gastronomia;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;

// JSON mínimo de prueba: no realiza conexiones ni usa datos reales de personas.
public class ApiModelsTest {
    @Test public void parsesDomainJson() {
        ApiModels.Meal meal = new Gson().fromJson("{\"idMeal\":\"1\",\"strMeal\":\"Sopa\",\"strIngredient1\":\"Carrot\",\"strMeasure1\":\"2\",\"strIngredient2\":\" \",\"strIngredient3\":\"Salt\"}", ApiModels.Meal.class);
        assertEquals("1", meal.idMeal);
        assertTrue(meal.ingredientsText().contains("Carrot — 2"));
        assertTrue(meal.ingredientsText().contains("Salt"));
        assertEquals(2, meal.ingredientsText().split("\n").length);
    }

    @Test public void emptyResponsesAreRepresentable() {
        ApiModels.MealsResponse empty = new Gson().fromJson("{\"meals\":null}", ApiModels.MealsResponse.class);
        assertNull(empty.meals);
    }
}


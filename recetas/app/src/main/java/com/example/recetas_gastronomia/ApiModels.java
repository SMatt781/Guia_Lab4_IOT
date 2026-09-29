package com.example.recetas_gastronomia;

import java.util.List;

// POJO: nombres iguales a las claves del JSON; Gson hace la conversión.
public final class ApiModels {
    public static class CategoriesResponse { public List<Category> categories; }
    public static class MealsResponse { public List<Meal> meals; }
    public static class Category {
        public String idCategory, strCategory, strCategoryThumb, strCategoryDescription;
    }
    public static class Meal {
        public String idMeal, strMeal, strMealThumb, strCategory, strArea, strInstructions;
        public String strIngredient1, strIngredient2, strIngredient3, strIngredient4,
                strIngredient5, strIngredient6, strIngredient7, strIngredient8,
                strIngredient9, strIngredient10, strIngredient11, strIngredient12,
                strIngredient13, strIngredient14, strIngredient15, strIngredient16,
                strIngredient17, strIngredient18, strIngredient19, strIngredient20;
        public String strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
                strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
                strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
                strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20;

        public String ingredientsText() {
            String[] ingredients = {strIngredient1, strIngredient2, strIngredient3,
                    strIngredient4, strIngredient5, strIngredient6, strIngredient7,
                    strIngredient8, strIngredient9, strIngredient10, strIngredient11,
                    strIngredient12, strIngredient13, strIngredient14, strIngredient15,
                    strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20};
            String[] measures = {strMeasure1, strMeasure2, strMeasure3, strMeasure4,
                    strMeasure5, strMeasure6, strMeasure7, strMeasure8, strMeasure9,
                    strMeasure10, strMeasure11, strMeasure12, strMeasure13, strMeasure14,
                    strMeasure15, strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20};
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < ingredients.length; i++) {
                if (ingredients[i] != null && !ingredients[i].trim().isEmpty()) {
                    text.append("• ").append(ingredients[i].trim());
                    if (measures[i] != null && !measures[i].trim().isEmpty()) {
                        text.append(" — ").append(measures[i].trim());
                    }
                    text.append("\n");
                }
            }
            return text.toString();
        }
    }
}

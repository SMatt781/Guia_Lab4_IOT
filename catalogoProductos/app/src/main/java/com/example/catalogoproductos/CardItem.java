package com.example.catalogoproductos;

// Datos de una fila. No es una View y no contiene lógica de navegación.
public class CardItem {
    public final String id;
    public final String title;
    public final String subtitle;
    public final String imageUrl;

    public CardItem(String id, String title, String subtitle, String imageUrl) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
    }
}

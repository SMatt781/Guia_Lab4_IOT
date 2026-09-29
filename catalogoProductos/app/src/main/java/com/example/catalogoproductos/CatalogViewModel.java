package com.example.catalogoproductos;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

// Semana 6: estado compartido por los fragmentos de la misma Activity.
public class CatalogViewModel extends ViewModel {
    public final MutableLiveData<List<CardItem>> groups = new MutableLiveData<>();
    public final MutableLiveData<List<CardItem>> items = new MutableLiveData<>();
    public final MutableLiveData<DetailData> detail = new MutableLiveData<>();
    public String category = "";
    public String query = "";
    public String selectedId = "";

    public void changeFilter(String newCategory, String newQuery) {
        if (!category.equals(newCategory) || !query.equals(newQuery)) {
            category = newCategory;
            query = newQuery;
            items.setValue(null);
        }
    }

    public void selectId(String id) {
        if (!selectedId.equals(id)) {
            selectedId = id;
            detail.setValue(null);
        }
    }
}

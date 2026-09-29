package com.example.recetas_gastronomia;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.example.recetas_gastronomia.databinding.ItemCardBinding;

// Semana 5: Adapter + ViewHolder + item_card.xml.
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {
    public interface OnCardClick { void onClick(CardItem item); }
    private final List<CardItem> items = new ArrayList<>();
    private final OnCardClick listener;

    public CardAdapter(OnCardClick listener) { this.listener = listener; }

    public void submit(List<CardItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardBinding binding = ItemCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CardHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        CardItem item = items.get(position);
        holder.binding.title.setText(item.title);
        holder.binding.subtitle.setText(item.subtitle);
        ImageLoader.load(holder.binding.photo, item.imageUrl);
        // Se captura el objeto actual, no una posición que pueda quedar obsoleta.
        holder.binding.getRoot().setOnClickListener(view -> listener.onClick(item));
    }

    @Override public int getItemCount() { return items.size(); }

    static class CardHolder extends RecyclerView.ViewHolder {
        final ItemCardBinding binding;
        CardHolder(ItemCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

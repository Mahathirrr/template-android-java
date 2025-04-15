package com.example.template_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template_project.R;
import com.example.template_project.model.Deck;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private List<Deck> decks;
    private final OnDeckClickListener listener;

    public interface OnDeckClickListener {
        void onDeckClick(Deck deck);
        void onEditDeck(Deck deck);
        void onDeleteDeck(Deck deck);
    }

    public DeckAdapter(List<Deck> decks, OnDeckClickListener listener) {
        this.decks = decks;
        this.listener = listener;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        Deck deck = decks.get(position);
        holder.bind(deck, listener);
    }

    @Override
    public int getItemCount() {
        return decks != null ? decks.size() : 0;
    }

    public static class DeckViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDeckName;
        private final TextView tvDeckDescription;
        private final TextView tvCardCount;
        private final ImageView ivEditDeck;
        private final ImageView ivDeleteDeck;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeckName = itemView.findViewById(R.id.tvDeckName);
            tvDeckDescription = itemView.findViewById(R.id.tvDeckDescription);
            tvCardCount = itemView.findViewById(R.id.tvCardCount);
            ivEditDeck = itemView.findViewById(R.id.ivEditDeck);
            ivDeleteDeck = itemView.findViewById(R.id.ivDeleteDeck);
        }

        public void bind(final Deck deck, final OnDeckClickListener listener) {
            tvDeckName.setText(deck.getName());
            tvDeckDescription.setText(deck.getDescription());
            
            int cardCount = deck.getCardCount();
            tvCardCount.setText(cardCount + " " + (cardCount == 1 ? "card" : "cards"));

            itemView.setOnClickListener(v -> listener.onDeckClick(deck));
            ivEditDeck.setOnClickListener(v -> listener.onEditDeck(deck));
            ivDeleteDeck.setOnClickListener(v -> listener.onDeleteDeck(deck));
        }
    }
}
package com.example.template_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template_project.R;
import com.example.template_project.model.Flashcard;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Flashcard> cards;
    private final OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(Flashcard card);
        void onEditCard(Flashcard card);
        void onDeleteCard(Flashcard card);
        void onFlipCard(CardViewHolder holder, Flashcard card);
    }

    public CardAdapter(List<Flashcard> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    public void setCards(List<Flashcard> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Flashcard card = cards.get(position);
        holder.bind(card, listener);
    }

    @Override
    public int getItemCount() {
        return cards != null ? cards.size() : 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCardFront;
        private final TextView tvCardBack;
        private final ImageView ivFlipCard;
        private final ImageView ivEditCard;
        private final ImageView ivDeleteCard;
        private boolean isShowingBack = false;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardFront = itemView.findViewById(R.id.tvCardFront);
            tvCardBack = itemView.findViewById(R.id.tvCardBack);
            ivFlipCard = itemView.findViewById(R.id.ivFlipCard);
            ivEditCard = itemView.findViewById(R.id.ivEditCard);
            ivDeleteCard = itemView.findViewById(R.id.ivDeleteCard);
        }

        public void bind(final Flashcard card, final OnCardClickListener listener) {
            tvCardFront.setText(card.getFront());
            tvCardBack.setText(card.getBack());
            tvCardBack.setVisibility(isShowingBack ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> listener.onCardClick(card));
            ivEditCard.setOnClickListener(v -> listener.onEditCard(card));
            ivDeleteCard.setOnClickListener(v -> listener.onDeleteCard(card));
            ivFlipCard.setOnClickListener(v -> {
                isShowingBack = !isShowingBack;
                listener.onFlipCard(this, card);
            });
        }

        public void flipCard() {
            isShowingBack = !isShowingBack;
            tvCardBack.setVisibility(isShowingBack ? View.VISIBLE : View.GONE);
        }

        public boolean isShowingBack() {
            return isShowingBack;
        }
    }
}
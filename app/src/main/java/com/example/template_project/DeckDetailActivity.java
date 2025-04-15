package com.example.template_project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template_project.adapter.CardAdapter;
import com.example.template_project.data.DataManager;
import com.example.template_project.model.Deck;
import com.example.template_project.model.Flashcard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DeckDetailActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {

    public static final String EXTRA_DECK_ID = "extra_deck_id";

    private TextView tvDeckTitle;
    private TextView tvDeckDescription;
    private RecyclerView rvCards;
    private TextView tvEmptyCards;
    private Button btnStartQuiz;
    private FloatingActionButton fabAddCard;
    private CardAdapter cardAdapter;
    private DataManager dataManager;
    private Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_detail);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        tvDeckTitle = findViewById(R.id.tvDeckTitle);
        tvDeckDescription = findViewById(R.id.tvDeckDescription);
        rvCards = findViewById(R.id.rvCards);
        tvEmptyCards = findViewById(R.id.tvEmptyCards);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        fabAddCard = findViewById(R.id.fabAddCard);

        // Initialize data manager
        dataManager = DataManager.getInstance(this);

        // Get deck ID from intent
        String deckId = getIntent().getStringExtra(EXTRA_DECK_ID);
        if (deckId == null) {
            finish();
            return;
        }

        // Load deck
        deck = dataManager.getDeckById(deckId);
        if (deck == null) {
            finish();
            return;
        }

        // Set up deck info
        tvDeckTitle.setText(deck.getName());
        tvDeckDescription.setText(deck.getDescription());

        // Set up RecyclerView
        rvCards.setLayoutManager(new LinearLayoutManager(this));
        updateCardList();

        // Set up click listeners
        fabAddCard.setOnClickListener(v -> showAddEditCardDialog(null));
        btnStartQuiz.setOnClickListener(v -> {
            if (deck.getCardCount() == 0) {
                showMessage("You need to add cards to this deck before starting a quiz");
            } else {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_DECK_ID, deck.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deck != null) {
            // Refresh deck data
            deck = dataManager.getDeckById(deck.getId());
            if (deck != null) {
                updateCardList();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCardList() {
        List<Flashcard> cards = deck.getCards();
        if (cardAdapter == null) {
            cardAdapter = new CardAdapter(cards, this);
            rvCards.setAdapter(cardAdapter);
        } else {
            cardAdapter.setCards(cards);
        }

        // Show empty state if no cards
        if (cards.isEmpty()) {
            tvEmptyCards.setVisibility(View.VISIBLE);
            rvCards.setVisibility(View.GONE);
        } else {
            tvEmptyCards.setVisibility(View.GONE);
            rvCards.setVisibility(View.VISIBLE);
        }
    }

    private void showAddEditCardDialog(final Flashcard card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_card, null);
        builder.setView(dialogView);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etCardFront = dialogView.findViewById(R.id.etCardFront);
        EditText etCardBack = dialogView.findViewById(R.id.etCardBack);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Set dialog title and pre-fill fields if editing
        if (card != null) {
            tvDialogTitle.setText(R.string.edit_card);
            etCardFront.setText(card.getFront());
            etCardBack.setText(card.getBack());
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String front = etCardFront.getText().toString().trim();
            String back = etCardBack.getText().toString().trim();

            if (front.isEmpty()) {
                etCardFront.setError("Question cannot be empty");
                return;
            }

            if (back.isEmpty()) {
                etCardBack.setError("Answer cannot be empty");
                return;
            }

            if (card == null) {
                // Create new card
                Flashcard newCard = new Flashcard(front, back);
                dataManager.addCardToDeck(deck.getId(), newCard);
            } else {
                // Update existing card
                card.setFront(front);
                card.setBack(back);
                dataManager.updateCard(deck.getId(), card);
            }

            // Refresh deck data
            deck = dataManager.getDeckById(deck.getId());
            updateCardList();
            dialog.dismiss();
        });
    }

    private void showDeleteCardConfirmation(final Flashcard card) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Card")
                .setMessage(R.string.confirm_delete_card)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dataManager.deleteCard(deck.getId(), card.getId());
                    // Refresh deck data
                    deck = dataManager.getDeckById(deck.getId());
                    updateCardList();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showMessage(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // CardAdapter.OnCardClickListener implementation
    @Override
    public void onCardClick(Flashcard card) {
        // Do nothing or implement preview
    }

    @Override
    public void onEditCard(Flashcard card) {
        showAddEditCardDialog(card);
    }

    @Override
    public void onDeleteCard(Flashcard card) {
        showDeleteCardConfirmation(card);
    }

    @Override
    public void onFlipCard(CardAdapter.CardViewHolder holder, Flashcard card) {
        holder.flipCard();
    }
}
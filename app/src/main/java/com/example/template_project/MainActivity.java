package com.example.template_project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template_project.adapter.DeckAdapter;
import com.example.template_project.data.DataManager;
import com.example.template_project.model.Deck;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DeckAdapter.OnDeckClickListener {

    private RecyclerView rvDecks;
    private TextView tvEmptyDecks;
    private Button btnCreateDeck;
    private FloatingActionButton fabQuizMode;
    private DeckAdapter deckAdapter;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        rvDecks = findViewById(R.id.rvDecks);
        tvEmptyDecks = findViewById(R.id.tvEmptyDecks);
        btnCreateDeck = findViewById(R.id.btnCreateDeck);
        fabQuizMode = findViewById(R.id.fabQuizMode);

        // Initialize data manager
        dataManager = DataManager.getInstance(this);

        // Set up RecyclerView
        rvDecks.setLayoutManager(new LinearLayoutManager(this));
        updateDeckList();

        // Set up click listeners
        btnCreateDeck.setOnClickListener(v -> showAddEditDeckDialog(null));
        fabQuizMode.setOnClickListener(v -> {
            List<Deck> decks = dataManager.getAllDecks();
            if (decks.isEmpty()) {
                showMessage("You need to create at least one deck with cards to start Quiz Mode");
            } else {
                // TODO: Implement quiz mode selection
                showMessage("Quiz Mode will be implemented in the next version");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDeckList();
    }

    private void updateDeckList() {
        List<Deck> decks = dataManager.getAllDecks();
        if (deckAdapter == null) {
            deckAdapter = new DeckAdapter(decks, this);
            rvDecks.setAdapter(deckAdapter);
        } else {
            deckAdapter.setDecks(decks);
        }

        // Show empty state if no decks
        if (decks.isEmpty()) {
            tvEmptyDecks.setVisibility(View.VISIBLE);
            rvDecks.setVisibility(View.GONE);
        } else {
            tvEmptyDecks.setVisibility(View.GONE);
            rvDecks.setVisibility(View.VISIBLE);
        }
    }

    private void showAddEditDeckDialog(final Deck deck) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_deck, null);
        builder.setView(dialogView);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etDeckName = dialogView.findViewById(R.id.etDeckName);
        EditText etDeckDescription = dialogView.findViewById(R.id.etDeckDescription);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Set dialog title and pre-fill fields if editing
        if (deck != null) {
            tvDialogTitle.setText(R.string.edit);
            etDeckName.setText(deck.getName());
            etDeckDescription.setText(deck.getDescription());
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = etDeckName.getText().toString().trim();
            String description = etDeckDescription.getText().toString().trim();

            if (name.isEmpty()) {
                etDeckName.setError("Deck name cannot be empty");
                return;
            }

            if (deck == null) {
                // Create new deck
                Deck newDeck = new Deck(name, description);
                dataManager.addDeck(newDeck);
            } else {
                // Update existing deck
                deck.setName(name);
                deck.setDescription(description);
                dataManager.updateDeck(deck);
            }

            updateDeckList();
            dialog.dismiss();
        });
    }

    private void showDeleteDeckConfirmation(final Deck deck) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Deck")
                .setMessage(R.string.confirm_delete_deck)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dataManager.deleteDeck(deck.getId());
                    updateDeckList();
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

    // DeckAdapter.OnDeckClickListener implementation
    @Override
    public void onDeckClick(Deck deck) {
        Intent intent = new Intent(this, DeckDetailActivity.class);
        intent.putExtra(DeckDetailActivity.EXTRA_DECK_ID, deck.getId());
        startActivity(intent);
    }

    @Override
    public void onEditDeck(Deck deck) {
        showAddEditDeckDialog(deck);
    }

    @Override
    public void onDeleteDeck(Deck deck) {
        showDeleteDeckConfirmation(deck);
    }
}
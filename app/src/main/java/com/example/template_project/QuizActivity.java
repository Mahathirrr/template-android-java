package com.example.template_project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.template_project.data.DataManager;
import com.example.template_project.model.Deck;
import com.example.template_project.model.Flashcard;
import com.example.template_project.model.QuizResult;

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_DECK_ID = "extra_deck_id";

    private TextView tvQuizTitle;
    private TextView tvProgress;
    private CardView cardFront;
    private CardView cardBack;
    private TextView tvCardQuestion;
    private TextView tvCardAnswer;
    private Button btnShowAnswer;
    private Button btnPrevious;
    private Button btnNext;
    private Button btnCorrect;
    private Button btnIncorrect;

    private DataManager dataManager;
    private Deck deck;
    private List<Flashcard> quizCards;
    private int currentCardIndex = 0;
    private boolean isShowingAnswer = false;
    private QuizResult quizResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvProgress = findViewById(R.id.tvProgress);
        cardFront = findViewById(R.id.cardFront);
        cardBack = findViewById(R.id.cardBack);
        tvCardQuestion = findViewById(R.id.tvCardQuestion);
        tvCardAnswer = findViewById(R.id.tvCardAnswer);
        btnShowAnswer = findViewById(R.id.btnShowAnswer);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnCorrect = findViewById(R.id.btnCorrect);
        btnIncorrect = findViewById(R.id.btnIncorrect);

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
        if (deck == null || deck.getCardCount() == 0) {
            finish();
            return;
        }

        // Set up quiz title
        tvQuizTitle.setText(String.format("%s Quiz", deck.getName()));

        // Get randomized cards for quiz
        quizCards = dataManager.getRandomizedCards(deckId);
        
        // Initialize quiz result
        quizResult = new QuizResult(deckId, quizCards.size());

        // Display first card
        updateCardDisplay();

        // Set up click listeners
        btnShowAnswer.setOnClickListener(v -> showAnswer());
        btnPrevious.setOnClickListener(v -> showPreviousCard());
        btnNext.setOnClickListener(v -> showNextCard());
        btnCorrect.setOnClickListener(v -> {
            quizResult.incrementCorrectAnswers();
            showNextCard();
        });
        btnIncorrect.setOnClickListener(v -> {
            quizResult.addIncorrectCardId(quizCards.get(currentCardIndex).getId());
            showNextCard();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCardDisplay() {
        if (quizCards.isEmpty()) {
            finish();
            return;
        }

        Flashcard currentCard = quizCards.get(currentCardIndex);
        tvCardQuestion.setText(currentCard.getFront());
        tvCardAnswer.setText(currentCard.getBack());
        
        // Update progress text
        tvProgress.setText(String.format("Card %d of %d", currentCardIndex + 1, quizCards.size()));
        
        // Reset card state
        isShowingAnswer = false;
        cardFront.setVisibility(View.VISIBLE);
        cardBack.setVisibility(View.GONE);
        btnShowAnswer.setVisibility(View.VISIBLE);
        btnCorrect.setVisibility(View.GONE);
        btnIncorrect.setVisibility(View.GONE);
        
        // Enable/disable previous button
        btnPrevious.setEnabled(currentCardIndex > 0);
        
        // Change next button text on last card
        if (currentCardIndex == quizCards.size() - 1) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText(R.string.next_card);
        }
    }

    private void showAnswer() {
        isShowingAnswer = true;
        
        // Flip animation
        cardFront.animate()
                .rotationY(90)
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    cardFront.setVisibility(View.GONE);
                    cardBack.setVisibility(View.VISIBLE);
                    cardBack.setRotationY(-90);
                    cardBack.animate()
                            .rotationY(0)
                            .setDuration(200)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                })
                .start();
        
        // Show correct/incorrect buttons
        btnShowAnswer.setVisibility(View.GONE);
        btnCorrect.setVisibility(View.VISIBLE);
        btnIncorrect.setVisibility(View.VISIBLE);
    }

    private void showPreviousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            updateCardDisplay();
        }
    }

    private void showNextCard() {
        if (currentCardIndex < quizCards.size() - 1) {
            currentCardIndex++;
            updateCardDisplay();
        } else {
            // End of quiz
            finishQuiz();
        }
    }

    private void finishQuiz() {
        // Save quiz result
        dataManager.saveQuizResult(quizResult);
        
        // Show results dialog
        showQuizResultsDialog();
    }

    private void showQuizResultsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quiz_results, null);
        builder.setView(dialogView);

        TextView tvQuizScore = dialogView.findViewById(R.id.tvQuizScore);
        Button btnBackToDecks = dialogView.findViewById(R.id.btnBackToDecks);
        Button btnRetryQuiz = dialogView.findViewById(R.id.btnRetryQuiz);

        // Set score
        String scoreText = getString(R.string.quiz_score, quizResult.getCorrectAnswers(), quizResult.getTotalCards());
        tvQuizScore.setText(scoreText);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        btnBackToDecks.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        
        btnRetryQuiz.setOnClickListener(v -> {
            dialog.dismiss();
            // Reset quiz
            quizCards = dataManager.getRandomizedCards(deck.getId());
            quizResult = new QuizResult(deck.getId(), quizCards.size());
            currentCardIndex = 0;
            updateCardDisplay();
        });
    }
}
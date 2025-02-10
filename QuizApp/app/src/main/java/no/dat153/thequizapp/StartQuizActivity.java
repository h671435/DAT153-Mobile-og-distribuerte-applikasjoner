package no.dat153.thequizapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class StartQuizActivity extends AppCompatActivity {
    private TextView counterText;
    private int antallRiktig;
    private int antallGjett;
    private ImageView imageView;
    private Button buttonA, buttonB, buttonC;
    private GalleryBilde currentBilde;
    Set<Integer> brukteBilder = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        counterText = findViewById(R.id.counterText);

        imageView = findViewById(R.id.quizBilde);
        buttonA = findViewById(R.id.answer_A);
        buttonB = findViewById(R.id.answer_B);
        buttonC = findViewById(R.id.answer_C);

        oppdaterQuiz();

        buttonA.setOnClickListener(this::knappeTrykk);
        buttonB.setOnClickListener(this::knappeTrykk);
        buttonC.setOnClickListener(this::knappeTrykk);
    }

    private void knappeTrykk(View view) {
        Button clickedButton = (Button) view;
        String buttonText = clickedButton.getText().toString();

        antallGjett++;
        if (buttonText.equals(currentBilde.getNavn())) {
            antallRiktig++;
            clickedButton.setBackgroundColor(Color.GREEN);
        } else {
            clickedButton.setBackgroundColor(Color.RED);
            if (buttonA.getText().toString().equals(currentBilde.getNavn())) {
                buttonA.setBackgroundColor(Color.GREEN);
            } else if (buttonB.getText().toString().equals(currentBilde.getNavn())) {
                buttonB.setBackgroundColor(Color.GREEN);
            } else if (buttonC.getText().toString().equals(currentBilde.getNavn())) {
                buttonC.setBackgroundColor(Color.GREEN);
            }
        }

        if (buttonA != clickedButton && !buttonA.getText().toString().equals(currentBilde.getNavn()))
            buttonA.setBackgroundColor(Color.RED);
        if (buttonB != clickedButton && !buttonB.getText().toString().equals(currentBilde.getNavn()))
            buttonB.setBackgroundColor(Color.RED);
        if (buttonC != clickedButton && !buttonC.getText().toString().equals(currentBilde.getNavn()))
            buttonC.setBackgroundColor(Color.RED);

        updateCounter();
        // Noe magi som gjør at koden venter et sekund
        new android.os.Handler(Looper.getMainLooper()).postDelayed(this::oppdaterQuiz, 1000);
    }

    private void oppdaterQuiz() {
        resetFarger();
        if (brukteBilder.size() == QuizApp.getInstance().getBilder().size()) {
            Toast.makeText(this, "Du slayet denne quizzen asso", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Scoren din ble HEILE " + antallRiktig + "/" + antallGjett + "!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Starter quiz på nytt", Toast.LENGTH_LONG).show();

            // Restart aktiviteten
            finish();
            startActivity(getIntent());
            return;
        }

        Random random = new Random();

        int randomBilde;
        do {
            randomBilde = random.nextInt(QuizApp.getInstance().getBilder().size());
        } while (brukteBilder.contains(randomBilde));

        brukteBilder.add(randomBilde);

        // Sett riktig svar
        currentBilde = QuizApp.getInstance().getBilder().get(randomBilde);
        imageView.setImageURI(currentBilde.getBildeUri());
        String correctAnswer = currentBilde.getNavn();

        // Velger to forskjelle feile svar
        Set<String> wrongAnswers = new HashSet<>();
        while (wrongAnswers.size() < 2) {
            int randomIndex = random.nextInt(QuizApp.getInstance().getBilder().size());
            String wrongAnswer = QuizApp.getInstance().getBilder().get(randomIndex).getNavn();
            if (!wrongAnswer.equals(correctAnswer)) {
                wrongAnswers.add(wrongAnswer);
            }
        }

        // Kombiner riktig og fei lsvar
        ArrayList<String> choices = new ArrayList<>(wrongAnswers);
        choices.add(correctAnswer);

        // Shuffle svvar
        Collections.shuffle(choices);

        buttonA.setText(choices.get(0));
        buttonB.setText(choices.get(1));
        buttonC.setText(choices.get(2));
    }

    private void resetFarger() {
        buttonA.setBackgroundColor(Color.parseColor("#5A4B98"));
        buttonB.setBackgroundColor(Color.parseColor("#5A4B98"));
        buttonC.setBackgroundColor(Color.parseColor("#5A4B98"));
    }

    private void updateCounter() {
        counterText.setText(antallRiktig + "/" + antallGjett);
    }
}
package no.dat153.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    int counter = 0;
    TextView counterNr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.ButtonA).setOnClickListener(v -> launchMainActivity());
        findViewById(R.id.ButtonB).setOnClickListener(v -> launchMainAndFinishCurrentActivity());
        counterNr = findViewById(R.id.counterNr);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("SecondActivity", "onResume");

        counter++;
        counterNr.setText(String.valueOf(counter));
    }

    public void launchMainActivity() {
        Log.d("SecondActivity", "Launching MainActivity");
        startActivity(new Intent(this, MainActivity.class));
    }

    public void launchMainAndFinishCurrentActivity() {
        Log.d("SecondActivity", "Launching MainActivity and finishing current activity");
        finish();
    }
}
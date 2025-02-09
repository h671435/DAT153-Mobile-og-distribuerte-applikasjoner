package no.dat153.thequizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import no.dat153.thequizapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        EdgeToEdge.enable(this);
        // setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.buttonStartQuiz.setOnClickListener(v -> {
            Log.d("MainActivity", "Launching QuizActivity");
            startActivity(new Intent(this, StartQuizActivity.class));
        });

        binding.buttonGallery.setOnClickListener(v -> {
            Log.d("MainActivity", "Launching GalleryActivity");
            startActivity(new Intent(this, GalleryActivity.class));
        });

        loadSavedImages();
        if (QuizApp.getInstance().getBilder().isEmpty()) {
            setUpGalleryBilder();
        }
    }

    private void loadSavedImages() {
        SharedPreferences sharedPreferences = getSharedPreferences("BildeData", MODE_PRIVATE);
        String savedImagesJson = sharedPreferences.getString("bildeURIer", null);

        QuizApp.getInstance().getBilder().clear();

        if (savedImagesJson != null) {
            ArrayList<HashMap<String, String>> bildeURIer = new Gson().fromJson(savedImagesJson, new TypeToken<ArrayList<HashMap<String, String>>>() {
            }.getType());

            if (bildeURIer != null) {
                for (HashMap<String, String> bildeData : bildeURIer) {
                    String uriString = bildeData.get("uri");
                    String navn = bildeData.get("navn");

                    QuizApp.getInstance().getBilder().add(new GalleryBilde(Uri.parse(uriString), navn));
                }
            }
        }
    }

    private void setUpGalleryBilder() {
        QuizApp.getInstance().getBilder().add(new GalleryBilde(getResourceUri(R.drawable.katt), "Katt"));
        QuizApp.getInstance().getBilder().add(new GalleryBilde(getResourceUri(R.drawable.hund), "Hund"));
        QuizApp.getInstance().getBilder().add(new GalleryBilde(getResourceUri(R.drawable.snow_leopard), "Snø-Hest"));
    }

    // Hjelpemetode for å konvertere R.drawable til Uri
    private Uri getResourceUri(int resId) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + resId);
    }
}
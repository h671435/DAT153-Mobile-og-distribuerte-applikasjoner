package com.example.quiz2.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quiz2.Database.ItemRepository;
import com.example.quiz2.Database.ItemRoomDatabase;
import com.example.quiz2.Entity.Item;
import com.example.quiz2.R;
import com.example.quiz2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ItemRepository repo;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        EdgeToEdge.enable(this);
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.GalleryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        });
        binding.StartQuizBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            startActivity(intent);
        });

        repo = new ItemRepository(getApplication());
        Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.snoe_hest);
        Item startItem = new Item("Start Image", imageUri.toString());
        repo.insert(startItem);


    }
}
package com.example.quiz2.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz2.Adapter.GalleryAdapter;
import com.example.quiz2.Database.ItemRepository;
import com.example.quiz2.Database.ItemRoomDatabase;
import com.example.quiz2.Entity.Item;
import com.example.quiz2.R;

import java.util.ArrayList;
import java.util.List;

import kotlin.random.AbstractPlatformRandom;

public class GalleryActivity extends AppCompatActivity {
    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        EdgeToEdge.enable(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the repository
        ItemRepository repo = new ItemRepository(getApplication());

        //
        adapter = new GalleryAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Observer
        repo.getAllItems().observe(this, items -> adapter.setItems(items));
    }
}
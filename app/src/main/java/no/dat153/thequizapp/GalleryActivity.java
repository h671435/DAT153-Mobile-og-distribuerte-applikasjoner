package no.dat153.thequizapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    ArrayList<GalleryBilde> bilder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.min_recycler_view);

        setUpGalleryBilder();

        GalleryAdapter adapter = new GalleryAdapter(this, bilder);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.button_sort).setOnClickListener(v -> {
            // Sort bilder by name
            bilder.sort(Comparator.comparing(GalleryBilde::getNavn));
        });

        findViewById(R.id.button_add_image).setOnClickListener(addImage());
    }

    private void setUpGalleryBilder() {
        bilder.add(new GalleryBilde(R.drawable.katt, "Katt"));
        bilder.add(new GalleryBilde(R.drawable.hund, "Hund"));
        bilder.add(new GalleryBilde(R.drawable.snow_leopard, "Snø-Hest"));
    }

    private View.OnClickListener addImage() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast message for testing
                Toast.makeText(GalleryActivity.this, "Add image", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
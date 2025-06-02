package com.ebookfrenzy.quizappeksamen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.quizappeksamen.Adapter.GalleryAdapter;
import com.ebookfrenzy.quizappeksamen.Database.QuizItem;
import com.ebookfrenzy.quizappeksamen.Database.QuizItemDAO;
import com.ebookfrenzy.quizappeksamen.Database.QuizItemDatabase;
import com.ebookfrenzy.quizappeksamen.ViewModel.QuizItemViewModel;
import com.ebookfrenzy.quizappeksamen.databinding.ActivityGalleryBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class GalleryActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private final ActivityResultLauncher<String> requestCameraPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            openCamera();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
        }
    });
    private QuizItemViewModel quizViewModel;
    private ActivityGalleryBinding binding;
    private Uri bildeUri;
    private QuizItemDatabase db;
    private QuizItemDAO dao;
    private ArrayList<QuizItem> quizItems;
    private boolean sortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                saveImageToDb(bildeUri);
            } else {
                Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                bildeUri = uri;
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                saveImageToDb(uri);
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });

        quizViewModel = new ViewModelProvider(this).get(QuizItemViewModel.class);

        // Inflater layouten via binding
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Håndter insets på toppnivå layout – antar at root layout i activity_gallery.xml har id "main"
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialiser datastruktur
        quizItems = new ArrayList<>();

        // RecyclerView setup
        RecyclerView recyclerView = binding.recyclerView;
        GalleryAdapter adapter = new GalleryAdapter(this, quizItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down)
        );

        quizViewModel.getAllQuizItems().observe(this, items -> {
            quizItems.clear();
            quizItems.addAll(items);
            adapter.notifyDataSetChanged();
        });

        onClickListenerKnapper(binding);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void onClickListenerKnapper(ActivityGalleryBinding binding) {
        binding.sortBilder.setOnClickListener(v -> {
            if (sortAscending) {
                quizItems.sort(Comparator.comparing(QuizItem::getImageName));
            } else {
                quizItems.sort((a, b) -> b.getImageName().compareTo(a.getImageName()));
            }
            sortAscending = !sortAscending;
            binding.recyclerView.getAdapter().notifyDataSetChanged();
        });

        binding.addImage.setOnClickListener(v -> addImage());
    }

    private void addImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tittel")
                .setPositiveButton("Filer", (dialog, which) -> {
                    getImageFromFiles();
                })
                .setNegativeButton("Kamera", (dialog, which) -> {
                    takePicture();
                })
                .show();
    }

    private void getImageFromFiles() {
        imagePickerLauncher.launch("image/*");
    }

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("CameraPermission", "Camera permission not granted, asking for permission");
            requestCameraPermission.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Log.d("GalleryActivity", "Attempting to open camera...");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if there is a camera app available
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("GalleryActivity", "Camera app found.");

            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("GalleryActivity", "Created image file: " + photoFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e("GalleryActivity", "Failed to create image file", e);
                Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                bildeUri = FileProvider.getUriForFile(
                        this,
                        "com.ebookfrenzy.quizappeksamen.provider", // Make sure this matches your manifest
                        photoFile
                );
                Log.d("GalleryActivity", "FileProvider URI: " + bildeUri.toString());

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, bildeUri);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                cameraLauncher.launch(takePictureIntent);
            } else {
                Log.e("GalleryActivity", "photoFile is null after creation attempt.");
                Toast.makeText(this, "Image file could not be created", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("GalleryActivity", "No camera app available on device.");
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void saveImageToDb(Uri bilde) {
        if (bilde == null) return;
        showInputDialog();
    }

    private void showInputDialog() {
        final EditText input = new EditText(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter Image Name")
                .setView(input)
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    String imageName = input.getText().toString().trim();
                    if (!imageName.isEmpty()) {
                        // Create and save the QuizItem using ViewModel
                        QuizItem newItem = new QuizItem(bildeUri.toString(), imageName);
                        quizViewModel.insert(newItem);
                        Toast.makeText(GalleryActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(GalleryActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }
}
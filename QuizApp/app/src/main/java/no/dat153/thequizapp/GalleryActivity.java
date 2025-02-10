package no.dat153.thequizapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity {

    private Uri bildeUri;

    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView recyclerView;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.min_recycler_view);


        adapter = new GalleryAdapter(this, QuizApp.getInstance().getBilder());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sorter listen
        findViewById(R.id.button_sort).setOnClickListener(v -> sorterBilder());
        findViewById(R.id.button_add_image).setOnClickListener(addImage());
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveImages();
    }

    void removeImage(int position) {
        if (position >= 0 && position < QuizApp.getInstance().getBilder().size()) {
            QuizApp.getInstance().getBilder().remove(position);
            adapter.notifyItemRemoved(position);
            saveImages();
        }
    }

    private void sorterBilder() {
        boolean isSorted = true;
        for (int i = 0; i < QuizApp.getInstance().getBilder().size() - 1; i++) {
            if (QuizApp.getInstance().getBilder().get(i).getNavn().compareTo(QuizApp.getInstance().getBilder().get(i + 1).getNavn()) > 0) {
                isSorted = false;
                break;
            }
        }

        if (isSorted) {
            QuizApp.getInstance().getBilder().sort((b1, b2) -> b2.getNavn().compareTo(b1.getNavn()));
        } else {
            QuizApp.getInstance().getBilder().sort(Comparator.comparing(GalleryBilde::getNavn));
        }

        adapter.notifyItemRangeChanged(0, QuizApp.getInstance().getBilder().size());
    }

    private void saveImages() {
        SharedPreferences sharedPreferences = getSharedPreferences("BildeData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<HashMap<String, String>> bildeURIer = new ArrayList<>();
        for (GalleryBilde bilde : QuizApp.getInstance().getBilder()) {
            HashMap<String, String> bildeData = new HashMap<>();
            bildeData.put("uri", bilde.getBildeUri().toString());
            bildeData.put("navn", bilde.getNavn());
            bildeURIer.add(bildeData);
        }

        editor.putString("bildeURIer", new Gson().toJson(bildeURIer));
        editor.apply();
    }

    private View.OnClickListener addImage() {
        return v -> visBildeValgDialog();
    }

    private void visBildeValgDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Velg bilde kamera eller bilde mobil").setMessage("Velg en handling").setPositiveButton("Ta et bilde", (dialog, which) -> openCamera()).setNegativeButton("Velg fra galleri", (dialog, which) -> openBilder()).setNeutralButton("Avbryt", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Sjekker om det finnes en app som kan ta bilder
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File bildeFil = createImageFile();
            if (bildeFil != null) {
                bildeUri = FileProvider.getUriForFile(this, "no.dat153.thequizapp.fileprovider", bildeFil);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, bildeUri);
                startActivityForResult(cameraIntent, 1);
            }
        }
    }

    private File createImageFile() {
        String tidspunkt = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile("JPEG_" + tidspunkt + "_", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { // Camera
                leggVedNavnPaaBildeSomBlirTatt(bildeUri);
            } else if (requestCode == 2 && data != null) {
                Uri selectedImageUri = data.getData();

                // Ensure we have permissions for persistent access
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);

                leggVedNavnPaaBildeSomBlirTatt(selectedImageUri);
            }
        }
    }

    private void leggVedNavnPaaBildeSomBlirTatt(Uri bildeUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Skriv inn hva du har tatt bilde av");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            String bildeNavn = input.getText().toString();
            if (!bildeNavn.isEmpty()) {
                GalleryBilde bilde = new GalleryBilde(bildeUri, bildeNavn);
                QuizApp.getInstance().getBilder().add(bilde);
                adapter.notifyItemInserted(QuizApp.getInstance().getBilder().size() - 1);
            }
        });

        builder.setNegativeButton("Avbryt", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openBilder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }
}
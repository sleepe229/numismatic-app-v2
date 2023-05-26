package ru.rut.cnn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.core.content.ContextCompat;


import org.tensorflow.lite.support.label.Category;

import java.io.IOException;

import ru.rut.cnn.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements RecognitionListener{
    private ActivityMainBinding binding;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualLauncher;
    private final String TAG = "MainActivity";
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerActivityForPickImage();

        binding.takePicture.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });

        binding.galleryView.setOnClickListener(v -> {
            pickVisualLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    private void registerActivityForPickImage() {
        pickVisualLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                try {
                    Category output = new BaseAnalyzer(getApplicationContext()).analyze(BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(uri)));
                    Log.i("MainActivity", output.getLabel());
                } catch (IOException e) {
                    Log.e("MainActivity", e.getMessage(), e);
                }

            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
    }

    @Override
    public void onResult(Category category) {
        Log.w(TAG, category.getLabel());
    }
}
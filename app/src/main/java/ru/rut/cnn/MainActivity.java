package ru.rut.cnn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity .result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import ru.rut.cnn.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualLauncher;
    private Coin output;

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void registerActivityForPickImage() {
        pickVisualLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                try {
                    output = new BaseAnalyzer(getApplicationContext()).analyze(BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(uri)));
                    Log.i("MainActivity", output.label);

                    binding.coinLabel.setText(output.label);
                    binding.coinImage.setImageDrawable(getDrawable(output.preview));
                    binding.coinDescription.setText(output.info_text); //Разобраться как выводить текст из файла
                } catch (IOException e) {
                    Log.e("MainActivity", e.getMessage(), e);
                }
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
    }

    @Override
    public void onResult(Coin category) {
        Log.w(TAG, category.label);
    }
}
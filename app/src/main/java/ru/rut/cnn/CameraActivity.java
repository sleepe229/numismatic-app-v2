package ru.rut.cnn;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.support.label.Category;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ru.rut.cnn.databinding.ActivityCameraBinding;

public class CameraActivity extends AppCompatActivity implements RecognitionListener {
    private ActivityCameraBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.takePicture.setOnClickListener(v -> takePicture());
        binding.backActivity.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            startActivity(intent);
        });

        openCamera();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityResultLauncher<String[]> launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                result.forEach((permission, res) -> {
                    if (permission.equals(Manifest.permission.CAMERA) && res) {
                        bindCamera();
                    }
                });
            });
            launcher.launch(new String[]{Manifest.permission.CAMERA});
        } else {
            bindCamera();
        }
    }

    private void bindCamera() {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder().build();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                preview.setSurfaceProvider(binding.cameraView.getSurfaceProvider());

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        },
        ContextCompat.getMainExecutor(this));
    }

    private void takePicture() {
        String name = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CNN-Images");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                String text = "Success! File location: " + outputFileResults.getSavedUri();
                Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                Log.d(TAG, text);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                String text = "Error: " + exception.getMessage();
                Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
                Log.e(TAG, text);
            }
        });
    }

    @Override
    public void onResult(Category category) {
        Log.w(TAG, category.getLabel());
    }
}

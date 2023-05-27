package ru.rut.cnn;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;

import ru.rut.cnn.ml.Model;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private Uri imageUri;
    private RecognitionListener listener;
    private Context context;

    public ImageAnalyzer(Context context, RecognitionListener listener, Uri uri) {
        this.listener = listener;
        this.imageUri = uri;
        this.context = context;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            if (bitmap == null) {
                Log.e("ImageAnalyzer", "Error: Bitmap is null");
                return;
            }

            listener.onResult(new BaseAnalyzer(context).analyze(bitmap));
        } catch (IOException e) {
            Log.e("ImageAnalyzer", "error", e);
        }
    }
}

package ru.rut.cnn;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ru.rut.cnn.ml.Model;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private static final int IMAGE_WIDTH = 150;
    private static final int IMAGE_HEIGHT = 150;
    private RecognitionListener listener;
    private Model model;




    public ImageAnalyzer(Context context, RecognitionListener listener) {
        try {
            this.listener = listener;
            this.model = Model.newInstance(context);
        } catch (IOException e) {
            Log.e("ImageAnalyzer", "Error: " + e.getMessage());
        }
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        Bitmap bitmap = image.toBitmap();


            try {
                if (bitmap == null) {
                    Log.e("ImageAnalyzer", "Error: Bitmap is null");
                    return;
                }

                Bitmap rescaledImage = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);

                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                tensorImage.load(rescaledImage);

                float[] output = model
                        .process(tensorImage.getTensorBuffer())
                        .getOutputFeature0AsTensorBuffer()
                        .getFloatArray();

                model.close();
                float maxOutput = output[0];
                int maxIndex = 0;
                for (int i = 1; i < output.length; i++) {
                    if (output[i] > maxOutput) {
                        maxOutput = output[i];
                        maxIndex = i;
                    }
                }
                Log.e("ImageAnalyzer", "result: " + maxIndex);

            } catch (Exception exception) {
                exception.printStackTrace();
        }
    }
}

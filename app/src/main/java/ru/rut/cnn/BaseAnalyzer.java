package ru.rut.cnn;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.IOException;

import ru.rut.cnn.ml.Model;

public class BaseAnalyzer {
    private static final int IMAGE_WIDTH = 150;
    private static final int IMAGE_HEIGHT = 150;
    private final Model model;

    public BaseAnalyzer(@NonNull Context context) throws IOException {
        this.model = Model.newInstance(context);
    }

    public Coin analyze(Bitmap bitmap) throws IllegalArgumentException{
        if (bitmap == null) {
            throw new IllegalArgumentException("Error: Bitmap is null");
        }

        Bitmap rescaledImage = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);

        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(rescaledImage);

        float[] output = model
                .process(tensorImage.getTensorBuffer())
                .getOutputFeature0AsTensorBuffer()
                .getFloatArray();

        model.close();

        float maxOutput = Float.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxOutput) {
                maxOutput = output[i];
                maxIndex = i;
            }
        }

        return new Coin(Coin.CLASS_NAMES[maxIndex], Coin.PREVIEWS[maxIndex]);
    }
}

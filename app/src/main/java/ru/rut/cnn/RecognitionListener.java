package ru.rut.cnn;

import org.tensorflow.lite.support.label.Category;

public interface RecognitionListener {
    void onResult(Category category);
}

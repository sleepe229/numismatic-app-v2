package ru.rut.cnn;

public class Coin {
    public static final String[] CLASS_NAMES = new String[] {"1", "10", "2", "5"};
    public static final Integer[] PREVIEWS = {R.drawable.random, R.drawable.random, R.drawable.random, R.drawable.easy};

    public final String label;
    public final Integer preview;

    public Coin(String label, Integer preview) {
        this.label = label;
        this.preview = preview;
    }
}

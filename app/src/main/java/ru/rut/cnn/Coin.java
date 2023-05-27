package ru.rut.cnn;

public class Coin {
    public static final String[] CLASS_NAMES = new String[] {"1 рубль", "10 рублей", "2 рубля", "5 рублей"};
    public static final Integer[] PREVIEWS = {R.drawable.one, R.drawable.ten, R.drawable.two, R.drawable.five};

    public final String label;
    public final Integer preview;

    public Coin(String label, Integer preview) {
        this.label = label;
        this.preview = preview;
    }
}

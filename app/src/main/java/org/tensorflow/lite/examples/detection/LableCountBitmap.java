package org.tensorflow.lite.examples.detection;

import android.graphics.Bitmap;

import java.util.HashMap;

public class LableCountBitmap {
    HashMap<String, Integer> labelCount;
    Bitmap bitmap;

    public LableCountBitmap() {
        this.labelCount = null;
        this.bitmap = null;
    }

    public LableCountBitmap(HashMap<String, Integer> labelCount, Bitmap bitmap) {
        this.labelCount = labelCount;
        this.bitmap = bitmap;
    }

    public HashMap<String, Integer> getLabelCount() {
        return labelCount;
    }

    public void setLabelCount(HashMap<String, Integer> labelCount) {
        this.labelCount = labelCount;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

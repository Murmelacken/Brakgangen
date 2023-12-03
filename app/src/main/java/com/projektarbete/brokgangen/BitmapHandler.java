package com.projektarbete.brokgangen;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BitmapHandler {
    private static List<Bitmap> bitmapList;
    public BitmapHandler(ArrayList<Bitmap> pass){
        bitmapList = pass;
    }
    static Bitmap bitmapHandlerGetBitmap(ArrayList<Bitmap> arrayList, int i){
        return arrayList.get(i);
    }
    static Bitmap speglaBild(Bitmap originalBitmap) {
        Matrix matrix = new Matrix();   // Skapa en spegelvänd version av Bitmap
        matrix.setScale(-1, 1); // Spegelvänd längs x-axeln

        return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
    }
    static Bitmap removeBackground(Bitmap bitmapToChange, int colorToRemove, int colorToSetPixelTo){
        // Create an array to store the pixel data
        int width = bitmapToChange.getWidth() ;
        int height = bitmapToChange.getHeight();
        int[] pixels = new int[width * height];
        bitmapToChange.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] newPixels = new int [width * height];
        int i = 0;
        for (int onePixel : pixels) {
            if (onePixel == colorToRemove){
                onePixel  = colorToSetPixelTo;
            }
            newPixels[i] = onePixel;
            i++;
        }
        Bitmap changedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888, true);
        changedBitmap.setPixels(newPixels, 0, width, 0, 0, width, height);
        if (newPixels == pixels){
            Log.d("debugging", "nya bitmappen är precis likadan"+newPixels);
        }
        return changedBitmap;

    }
}

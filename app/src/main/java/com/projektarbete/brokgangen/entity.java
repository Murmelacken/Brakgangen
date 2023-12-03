package com.projektarbete.brokgangen;

import android.content.Context;
import android.content.Entity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class entity {
    protected Bitmap mBp;
    protected int[] objectPosition = new int[2];
    protected RectF objectCBox = new RectF();
    protected int mSX, mSY;
    protected int objWidth, objHeight;
    protected int bitmapCounter;
    protected int imageCount;
    public int health;

    public entity(Context context, int SX, int SY, int[] placement){
        mSY = SY;
        mSX = SX;
        objectPosition = placement;

    }

    protected abstract void setObjectPosition(int[] placement);
    protected abstract void setObjectCBox();
    public abstract RectF getObjectCBox();
    public abstract Bitmap getCurrentStateBitmap();
    protected abstract void updateObjectPosition(int x, int y);
    public abstract void draw(Canvas canvas);

    public abstract void onCollision(RectF otherObject);
    /*private abstract static class myTest{
        public myTest(Context context, int asd){
            asd +=asd ;

        }
    };*/

}

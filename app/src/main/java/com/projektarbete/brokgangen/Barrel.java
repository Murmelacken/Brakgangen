package com.projektarbete.brokgangen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Barrel extends ImmovableEntity {
    public Bitmap mBp;
    public Barrel(Context context, int SX, int SY, int[] placement) {
        super(context, SX, SY, placement);
        mSX = SX;
        mSY = SY;
        importBitmapToClass(context);
        setObjectPosition(placement);
        mBp = importBitmapToClass(context);
    }

    private Bitmap importBitmapToClass(Context context){
        Bitmap bp = BitmapFactory.decodeResource(context.getResources(),R.drawable.tunna);
        bp = Bitmap.createScaledBitmap(bp, 75, 100, true);
        objWidth = bp.getWidth();
        objHeight = bp.getHeight();
        return bp;
    }
    @Override
    public void onCollision(RectF otherObject) {

    }
    @Override
    public void setObjectPosition(int[] plc){
        objectPosition = plc;
        setObjectCBox();
    }
    @Override
    public void setObjectCBox(){
        int left = objectPosition[0];
        int top = objectPosition[1];//objHeight*2/10;
        int right = objectPosition[0]+objWidth;
        int bottom = objectPosition[1]+objHeight;
        objectCBox = new RectF(left, top, right, bottom);
    }
    @Override
    public RectF getObjectCBox(){
        return objectCBox;
    }
    @Override
    public Bitmap getCurrentStateBitmap(){
        return mBp;
    }
}

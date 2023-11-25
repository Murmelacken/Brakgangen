package com.projektarbete.brokgangen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

public class passageWay extends immovableEntity{
    public Bitmap mBp;
    //private int mSX;
    //private int mSY;
    private int[] extendedPlacement;
    public passageWay(Context context, int SX, int SY, int[] placement, int i) {
        super(context, SX, SY, placement);
        mBp = importBitmap(context);
        extendedPlacement = new int[]{placement[0],placement[1], placement[0]+mBp.getWidth(),placement[1]+mBp.getHeight()};
        mBp = rotateWithInteger(mBp, i);
        //Log.d("bmp","grindway: " + getObjectCBox() + " bitmap: " + mBp);
    }

    private Bitmap importBitmap(Context c){
        Bitmap bp = BitmapFactory.decodeResource(c.getResources(), R.drawable.grind);
        bp = Bitmap.createScaledBitmap(bp, mSX / 3, mSY / 8, true);
        objWidth = bp.getWidth(); objHeight = bp.getHeight();
        return bp;
    }
    public Bitmap getCurrentStateBitmap() {
        return mBp;
    }
    private Bitmap rotateWithInteger(Bitmap oBitmap, int i){
        int sX = (i == 2 || i == 0) ? 1 : -1;
        int sY = (i == 1 || i == 3) ? -1 : 1;
        Matrix matris = new Matrix();   // Skapa en spegelvänd version av Bitmap
        matris.setScale(sX, sY); // Spegelvänd längs x-axeln, int fromX, int toX, int fromY, int toY
        //vänd enligt inlagd matris
        oBitmap = Bitmap.createBitmap(oBitmap, 0, 0, oBitmap.getWidth(), oBitmap.getHeight(), matris, true);
        objectCBox = createCorrectRect(extendedPlacement, sX, sY);
        //setObjectCBox();
        return oBitmap;
    }
    private RectF createCorrectRect(int[] xyPlacements, int rX, int rY){
        // case (1,1)
        int left = xyPlacements[0];
        int top = xyPlacements[1];
        int right = xyPlacements[2]; // kord_x + bredd
        int bottom = xyPlacements[3]; // kord_y + höjd
        if (rX == -1) {
            top = mSY-objHeight;
            bottom = mSY;


        }
        return new RectF(left, top, right, bottom);
    }
    @Override
    protected void setObjectCBox() {
        int left = objectPosition[0];
        int top = objectPosition[1];
        int right = objectPosition[0]+objWidth;
        int bottom = objectPosition[1]+objHeight;
        objectCBox = new RectF(left, top, right, bottom);
    }
    @Override
    public RectF getObjectCBox(){
        return objectCBox;
    }
    @Override
    public void onCollision(RectF otherObject) {

    }

}

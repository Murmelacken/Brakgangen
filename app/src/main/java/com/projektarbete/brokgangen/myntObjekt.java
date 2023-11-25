package com.projektarbete.brokgangen;

import static com.projektarbete.brokgangen.bitmapHandler.speglaBild;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class myntObjekt extends immovableEntity{
    protected Bitmap mBp;
    protected int[] objectPosition = new int[2];
    protected RectF objectCBox = new RectF();
    protected int mSX, mSY;
    protected int objWidth, objHeight;
    protected int bitmapCounter = 0;
    List<Bitmap> movingStateBitmaps = new ArrayList<>();
    protected int imageCount;
    private int delayCount = 0;
    public myntObjekt(Context context, int SX, int SY, int[] placement) {
        super(context, SX, SY, placement);
        importGuldPengar(context);
        imageCount = movingStateBitmaps.size()-1;
        objWidth = movingStateBitmaps.get(0).getWidth();
        objHeight = movingStateBitmaps.get(0).getHeight();
        setObjectPosition(placement);
        Log.d("debugging", "objectCBox: " + objectCBox + " objWidth: " + objWidth + " objHeight: " + objHeight);
        Log.d("debugging", "imagecoutn: " + imageCount);

        //bitmapHandler bh = new bitmapHandler(snurrandeMynt);

    }
    private void importGuldPengar(Context cont){
        movingStateBitmaps.add(BitmapFactory.decodeResource(cont.getResources(), R.drawable.guldpeng1));
        movingStateBitmaps.add(BitmapFactory.decodeResource(cont.getResources(), R.drawable.guldpeng2));
        movingStateBitmaps.add(BitmapFactory.decodeResource(cont.getResources(), R.drawable.guldpeng3));
        movingStateBitmaps.add(speglaBild(movingStateBitmaps.get(1)));
        //BitmapFactory.decodeResource(cont.getResources(), R.drawable.guldpeng2); guldPengs[1];
        movingStateBitmaps.add(speglaBild(movingStateBitmaps.get(0)));
        movingStateBitmaps.add(speglaBild(movingStateBitmaps.get(1)));
        //BitmapFactory.decodeResource(cont.getResources(), R.drawable.guldpeng3); guldPengs[0];
        movingStateBitmaps.add(movingStateBitmaps.get(2));
        movingStateBitmaps.add(movingStateBitmaps.get(1));
    }
    public Bitmap getCurrentStateBitmap(){
        if (bitmapCounter==imageCount){
            bitmapCounter = 0;
            delayCount = 0;
        }
        if (delayCount == 4) {bitmapCounter++;delayCount = 0;}
        Bitmap bp = movingStateBitmaps.get(bitmapCounter);
        delayCount++;
        return bp;
    }

    @Override
    protected void setObjectPosition(int[] placement) {
        objectPosition[0] = placement[0];
        objectPosition[1] = placement[1];
        setObjectCBox();
    }

    @Override
    protected void setObjectCBox() {
        int left = objectPosition[0];
        int top = objectPosition[1];
        int right = objectPosition[0]+objWidth;//snurrandeMynt.get(bitmapCounter).getWidth();
        int bottom = objectPosition[1]+objHeight;//snurrandeMynt.get(bitmapCounter).getHeight();
        objectCBox = new RectF(left,top,right,bottom);
    }
    @Override
    public RectF getObjectCBox(){
        return objectCBox;
    }
    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void onCollision(RectF otherObject) {

    }
    public void remove(){
        objectCBox=new RectF();
    }
}

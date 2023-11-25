package com.projektarbete.brokgangen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public class NPC extends movableEntity{
    protected int[] objectPosition = new int[2];
    protected RectF objectCBox = new RectF();
  //  protected int mSX, mSY;
    protected int objWidth, objHeight;
    protected int bitmapCounter;
    protected int imageCountMoving;
    protected int imageCountAttacking;
    public boolean attacking;
    ArrayList<Bitmap> movingStateBitmaps = new ArrayList<>();
    ArrayList<Bitmap> rightAttackingBitmaps = new ArrayList<>();
    ArrayList<Bitmap> leftAttackingBitmaps = new ArrayList<>();
    private int lastKnownX;
    public int health;
    private int delayCount;
    public NPC(Context context, int SX, int SY, int[] placement) {
        super(context, SX, SY, placement);
        //mSX = SX; mSY = SY;
        importAndDivide(context);
        health = 10;
        objWidth=movingStateBitmaps.get(0).getWidth();
        objHeight=movingStateBitmaps.get(0).getHeight();
        imageCountMoving = movingStateBitmaps.size()-1;
        imageCountAttacking = rightAttackingBitmaps.size()-1;
        setObjectPosition(placement);

    }

    private void importAndDivide(Context context){
        Bitmap bigMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.vatterattack); //100x74 BREDD x HÖJD
        int w1 = 0;
        int w = 72;//bigMap.getWidth()/4;
        int h = bigMap.getHeight()/2;
        movingStateBitmaps.add(Bitmap.createBitmap(bigMap,0,0,w,h));
        // Log.d("debugging", "stopp 1");
        movingStateBitmaps.add(Bitmap.createBitmap(bigMap, w-2,0,w-8,h)); //vatte[0]; //Bitmap.createBitmap(bigMap, 25,0,2*w,h);
        //Log.d("debugging", "stopp 2");
        movingStateBitmaps.add(Bitmap.createBitmap(bigMap,w*2-4,0,w-8,h)); //vatte[0]; //Bitmap.createBitmap(bigMap,49,0,3*w,h);
        movingStateBitmaps.add(speglaBild(movingStateBitmaps.get(0)));
        movingStateBitmaps.add(speglaBild(movingStateBitmaps.get(1)));
        movingStateBitmaps.add(speglaBild(movingStateBitmaps.get(2)));
        //Log.d("debugging", "stopp 3");
        int w2 = bigMap.getWidth()/2-11;
        int h2 = bigMap.getHeight()/2;
        rightAttackingBitmaps.add(Bitmap.createBitmap(bigMap,
                0,h2+2,
                w2,h2-2));
        //Log.d("debugging", "stopp 4");
        rightAttackingBitmaps.add(Bitmap.createBitmap(bigMap,
        w2-12,h2+2,
        w2,h2-2));
        //Log.d("debugging", "stopp 4" + vatteAttack[1] + vatteAttack[0]);
        Bitmap tmp = rightAttackingBitmaps.get(0);
        leftAttackingBitmaps.add(speglaBild(tmp));
        tmp = rightAttackingBitmaps.get(1);
        leftAttackingBitmaps.add(speglaBild(tmp));
    }

    private Bitmap speglaBild(Bitmap originalBitmap) {
        Matrix matrix = new Matrix();   // Skapa en spegelvänd version av Bitmap
        matrix.setScale(-1, 1); // Spegelvänd längs x-axeln
        return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
    }
    public Bitmap getCurrentStateBitmap(){
        Bitmap bp;
        if (attacking) {
            bitmapCounter = bitmapCounter>imageCountAttacking ? 0 : bitmapCounter;
            if (lastKnownX > objectPosition[0]){
                bp = rightAttackingBitmaps.get(bitmapCounter);
            }else{
                bp = leftAttackingBitmaps.get(bitmapCounter);
            }

        }else{
            bitmapCounter = bitmapCounter>imageCountMoving ? 0 : bitmapCounter;
            bp = movingStateBitmaps.get(bitmapCounter);
        }
        updateSaveDimensions(bp.getWidth(),bp.getHeight());
        delayCount++;
        if (delayCount == 4){bitmapCounter++; delayCount = 0;}
        //addToBitmapCounter(1);
        return bp;
    }
    private void updateSaveDimensions(int x, int y){
        objWidth = x;
        objHeight = y;
    }
    private void addToBitmapCounter(int i){
        bitmapCounter += i;
    }
    @Override
    protected void setObjectPosition(int[] placement) {
        objectPosition = placement;
        setObjectCBox();
    }
    public void updateObjectPosition(int x, int y) {
        objectPosition[0] += x;
        objectPosition[1] += y;
        setObjectCBox();
    }
    public void movement(int[] playerPosition) {
        lastKnownX = playerPosition[0];
        float dX = lastKnownX-objectPosition[0];
        float dY = playerPosition[1]-objectPosition[1];
       // float dC = (float) Math.hypot(dX,dY);
        attacking = (Math.abs(dX)<170 && Math.abs(dY)<150);
        //float minKonstant = 5.0f;
        float sendX = dX/100;// dX/dC*5;//dX/100;
        float sendY = dY/100;//dY/dC*5; //dY/100;
        updateObjectPosition((int)sendX, (int)sendY);
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
    public RectF getObjectCBox() {
        return objectCBox;
    }


    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void onCollision(RectF otherObject) {
        boolean chkX = objectPosition[0] >= (otherObject.left + (otherObject.width() / 2));
        boolean chkY = objectPosition[1] >= (otherObject.top + (otherObject.height() / 2)) ;
        int adjY = objHeight/20;
        int adjX = objWidth/20;
        //Log.d("debugging", "Funnen kollision chx: " + chkX + " chY: " + chkY);
        //Log.d("debugging", "otherOBjeckt: " + otherObject);
        if (chkY){ //inte perfekt metod, har nu delat upp kollision i fyra delar
            //objectPosition[1] += adjY;
            updateObjectPosition(0,adjY);
            setObjectPosition(new int[]{objectPosition[0],objectPosition[1]+adjY});
        }else{
            //objectPosition[1] -= adjY;
            updateObjectPosition(0,-adjY);
            setObjectPosition(new int[]{objectPosition[0],objectPosition[1]-adjY});
        }
        if (chkX){
            //objectPosition[0] += adjX;
            updateObjectPosition(adjX,0);
            setObjectPosition(new int[]{objectPosition[0]+adjX,objectPosition[1]});

        }else{
            //objectPosition[0] -= adjX;
            updateObjectPosition(-adjX,0);
            setObjectPosition(new int[]{objectPosition[0]-adjX,objectPosition[1]});
        }
    }
}

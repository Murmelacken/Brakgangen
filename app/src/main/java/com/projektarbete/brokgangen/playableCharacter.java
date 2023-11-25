package com.projektarbete.brokgangen;

import static com.projektarbete.brokgangen.bitmapHandler.removeBackground;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public class playableCharacter extends movableEntity{
        protected Bitmap mBp;
        protected int[] objectPosition = new int[2];
    public int[] newPosition = new int[2];
        protected RectF objectCBox = new RectF();
        private boolean isMoving = false;
        protected int mSX, mSY;
        protected int objWidth, objHeight;
        protected int bitmapCounter;
        protected int imageCount;
        public int health;
        ArrayList<Bitmap> movingStateBitmaps = new ArrayList<>();
    ArrayList<Bitmap> attackingStateBitmaps = new ArrayList<>();

    public playableCharacter(Context context, int SX, int SY, int[] placement) {
        super(context, SX, SY, placement);
        mSX = SX;
        mSY = SY;
        health = 20;
        setBitmapArrayList(context);
        setObjectPosition(placement);
        //setObjectCBox(new RectF(objectPosition[0],objectPosition[1],objectPosition[0]+objWidth,objectPosition[0]+objHeight));
        setObjectCBox();
        imageCount = movingStateBitmaps.size()-1;
    }
    private void setBitmapArrayList(Context c){
        Bitmap gubbe1 = BitmapFactory.decodeResource(c.getResources(), R.drawable.liten_ett);
        Bitmap gubbe2 = BitmapFactory.decodeResource(c.getResources(), R.drawable.liten_tva);
        Bitmap gubbe3 = BitmapFactory.decodeResource(c.getResources(), R.drawable.liten_tre);;
        gubbe1 = removeBackground(gubbe1,gubbe1.getPixel(0,0),0);
        gubbe2 = removeBackground(gubbe2,gubbe2.getPixel(0,0),0);
        gubbe3 = removeBackground(gubbe3,gubbe3.getPixel(0,0),0);
        movingStateBitmaps.add(gubbe1);
        movingStateBitmaps.add(gubbe2);
        movingStateBitmaps.add(gubbe3);
        objWidth = gubbe1.getWidth(); objHeight = gubbe1.getHeight();
        //memObjectBox = new RectF(position[0], position[1], position[0] + charRectangle[0], position[1]+charRectangle[1]);
        //Log.d("debugging", "width x height:" + charRectangle[0] + " " + charRectangle[1]);
    }


    public void movement(){
        int[] wP = getNewPos();
        int rim = 5;
        if (Math.abs(wP[0]-objectPosition[0]) > rim || Math.abs(wP[1]-objectPosition[1]) > rim){
            setMoving(true);
            float deltaX = newPosition[0] - objectPosition[0];
            float deltaY = newPosition[1] - objectPosition[1];
            // avståndet == hypotenusan
            float distance = (float) Math.hypot(deltaX, deltaY);
            // Normaliserar felet genom att dividera med avståndet
            // för att få enhetsvektorn (minsta enheten som sedan hanteras med skalär)
            float unitX = deltaX / distance;
            float unitY = deltaY / distance;
            // Ange hastighetskonstanten
            float speed = 10.0f;
            // Beräkna det faktiska avståndet att förflytta sig
            float moveX = unitX * speed;
            float moveY = unitY * speed;
            // Uppdatera spelarens position och
            updateObjectPosition((int)moveX,(int)moveY);
            setObjectCBox();
        }else{
            setMoving(false);
        }

    }
    @Override
    protected void setObjectPosition(int[] placement) {
        objectPosition = placement;
        setObjectCBox();
    }

    public int[] getObjectPosition(){
        return objectPosition;
    }

    @Override
    protected void setObjectCBox() {
        int left = objectPosition[0];
        int top = objectPosition[1];
        int right = objectPosition[0]+objWidth;
        int bottom = objectPosition[1]+objHeight;
        objectCBox = new RectF(left, top, right, bottom);
    }
    public RectF getObjectCBox(){
        return objectCBox;
    }

    @Override
    public Bitmap getCurrentStateBitmap() {
        Bitmap bp;
        if (bitmapCounter > imageCount){
            bitmapCounter = 0;
        }
        if (isMoving){
        bp = movingStateBitmaps.get(bitmapCounter);
        bitmapCounter++;
        } else{
            bp = movingStateBitmaps.get(0);
        }
        return bp;
    }

    @Override
    public void updateObjectPosition(int x, int y) {
        objectPosition[0] += x;
        objectPosition[1] += y;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void onCollision(RectF otherObject) {
        boolean chkX = objectPosition[0] >= otherObject.left + (otherObject.width() / 2);
        boolean chkY = objectPosition[1] >= otherObject.top + (otherObject.height() / 2);
        int adjY = objHeight/9;//mSY/80;
        int adjX = objWidth/9;//mSX/80;
        //Log.d("debugging", "Funnen kollision chx: " + chkX + " chY: " + chkY);
        //Log.d("debugging", "otherOBjeckt: " + otherObject);
        if (chkY){ //inte perfekt metod, har nu delat upp kollision i fyra delar
            //objectPosition[1] += adjY;
            updateObjectPosition(0,adjY);
            setNewObjectPosition(new int[]{objectPosition[0],objectPosition[1]+adjY});
        }else{
            //objectPosition[1] -= adjY;
            updateObjectPosition(0,-adjY);
            setNewObjectPosition(new int[]{objectPosition[0],objectPosition[1]-adjY});
        }
        if (chkX){
            //objectPosition[0] += adjX;
            updateObjectPosition(adjX,0);
            setNewObjectPosition(new int[]{objectPosition[0]+adjX,objectPosition[1]});

        }else{
            //objectPosition[0] -= adjX;
            updateObjectPosition(-adjX,0);
            setNewObjectPosition(new int[]{objectPosition[0]-adjX,objectPosition[1]});
        }
    }
    public void damage(int dmg){
        health -= dmg;
    }

    public boolean isMoving() {
        return isMoving;
    }
    public void runAcross(int speed){
            updateObjectPosition(speed,0);
    }
    public void setMoving(boolean yn) {
        isMoving = yn;
    }

    public void setNewObjectPosition(int[] ints) {
        newPosition = ints;
    }
    protected int[] getNewPos(){
        return newPosition;
    }
}

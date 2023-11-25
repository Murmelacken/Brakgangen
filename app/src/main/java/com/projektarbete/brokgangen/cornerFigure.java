package com.projektarbete.brokgangen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

public class cornerFigure extends immovableEntity{
    protected Bitmap mBp;
    protected int[] objectPosition;
   // private int[] newPosition = new int[2];
    protected RectF objectCBox = new RectF();
    //private boolean isMoving = false;
    protected int mSX, mSY;
    protected int objWidth, objHeight;
    protected int bitmapCounter;
    int walkableColor;
    protected int imageCount;
    //ArrayList<Bitmap> movingStateBitmaps = new ArrayList<>();
    //ArrayList<Bitmap> attackingStateBitmaps = new ArrayList<>();
    public cornerFigure(Context context, int SX, int SY,int[] placement, int i) {
        super(context, SX, SY,placement);
        objectPosition = placement;
        mSX = SX;
        mSY = SY;
        objectCBox = new RectF(0, 0, mSX/3, mSY/4);
        mBp = rotateWithInteger(i, importThePartTemplates(context));
        //objectCBox = createCorrectRect(i);
        //setObjectCBox();
        objWidth = mBp.getWidth(); objHeight = mBp.getHeight();
        walkableColor = mBp.getPixel(mBp.getWidth()/2, mBp.getHeight()/2);
        //Log.d("debugging", "objectCBox: " + objectCBox + " objWidth: " + objWidth + " objHeight: " + objHeight);
    }

    private Bitmap importThePartTemplates(Context context) {
        return BitmapFactory.decodeResource(context.getResources(),R.drawable.test1);
    }
    private Bitmap rotateWithInteger(int i, Bitmap oBitmap){
        int sX = (i == 1 || i == 0) ? 1 : -1;
        int sY = (i == 2 || i == 0) ? -1 : 1;
        // i=0 => (1,-1)
        // i=1 => (1,1)
        // i=2 => (-1,1)
        // i=3 => (1,-1)
        Matrix matris = new Matrix();   // Skapa en spegelvänd version av Bitmap
        matris.setScale(sX, sY); // Spegelvänd längs x-axeln, int fromX, int toX, int fromY, int toY
        //vänd enligt inlagd matris
        oBitmap = Bitmap.createBitmap(oBitmap, 0, 0, oBitmap.getWidth(), oBitmap.getHeight(), matris, true);
        objectCBox = createCorrectRect(sX,sY);
        return oBitmap;
    }
    private RectF createCorrectRect(int matriX, int matriY){
        RectF currentRect = getObjectCBox();
        float tW = currentRect.width();
        float tH = currentRect.height();
        // tW = tempWidth
        // fX = från X
        // tX = till X
        // Skapar ny RectF beroende på önskad rotation
        int fX=0; int tX=0; int fY=0; int tY=0;
        if (matriX == 1 && matriY == -1){
            //vänster övre
            fX = 0; fY=0; tX = (int) tW; tY = (int) tH;
        } else if(matriX == 1 && matriY == 1){
            //spegelvänd på X-axeln (nere till vänster nu)
            fX = 0; tX = (int)tW; tY = mSY; fY = tY-(int) tH;
        } else if(matriX == -1 && matriY == -1){ //spegelvänd Y-axel (1,-1)
            tX = mSX; fX = tX-(int)tW;  fY = 0; tY = (int)tH;
        } else if(matriX == -1 && matriY == 1){
            // Högra hörnet nere (-1,1) beror på originalbitmap
            tX = mSX; fX = tX-(int)tW; tY = mSY; fY = tY-(int)tH;
        }
        RectF newRect = new RectF (fX,fY,tX,tY);
        return newRect;
    }
    @Override
    protected void setObjectPosition(int[] placement) {

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
    public Bitmap getCurrentStateBitmap() {
        return mBp;
    }

    @Override
    protected void updateObjectPosition(int x, int y) {

    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void onCollision(RectF stationaryObject) {

    }

}

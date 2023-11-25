package com.projektarbete.brokgangen;

import static com.projektarbete.brokgangen.bitmapHandler.speglaBild;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class escapeTunnel extends immovableEntity{
    public escapeTunnel(Context context, int SX, int SY, int[] placement, int sida) {
        super(context, SX, SY, placement);
        mBp = BitmapFactory.decodeResource(context.getResources(),R.drawable.tounel4);
        objWidth = mBp.getWidth();
        objHeight = mBp.getHeight();
        objectPosition[1] = mSY/2;
        if (sida == 0){
            mBp = speglaBild(mBp);
            //placement[0] > mSX/2
            objectPosition[0] = mSX-objWidth;
        }else{
            objectPosition[0] = 0;
        }
        setObjectCBox();
    }

    @Override
    public Bitmap getCurrentStateBitmap(){
        return mBp;
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
    public void onCollision(RectF otherObject) {

    }
}

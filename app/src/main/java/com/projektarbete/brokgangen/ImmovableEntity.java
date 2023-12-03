package com.projektarbete.brokgangen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class ImmovableEntity extends entity {

    public ImmovableEntity(Context context, int SX, int SY, int[] placement) {
        super(context, SX, SY, placement);
    }

    @Override
    protected void setObjectPosition(int[] placement) {

    }

    @Override
    protected void setObjectCBox() {

    }

    @Override
    public RectF getObjectCBox() {
        return null;
    }

    @Override
    public Bitmap getCurrentStateBitmap() {
        return null;
    }

    @Override
    protected void updateObjectPosition(int x, int y) {

    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public abstract void onCollision(RectF otherObject);
}

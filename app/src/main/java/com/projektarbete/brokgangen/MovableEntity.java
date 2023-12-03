package com.projektarbete.brokgangen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class MovableEntity extends entity {
    private int faultX;
    private int faultY;
    protected int speedDivisor = 70;

    public MovableEntity(Context context, int SX, int SY, int[] placement) {
        super(context, SX, SY, placement);
    }
    @Override
    protected void setObjectPosition(int[] placement) {}
    @Override
    protected void setObjectCBox() {}
    @Override
    public void updateObjectPosition(int x, int y) {}
    public void movement(int[] playerPosition){}
    @Override
    public void draw(Canvas canvas) {}
    @Override
    public abstract void onCollision(RectF otherObject);
}

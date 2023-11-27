package com.projektarbete.brokgangen;

import android.content.Context;
import android.content.Entity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class gameHandler extends SurfaceView implements Runnable {
    private boolean game_running = false;
    private boolean game_pause = true;
    private Thread threadGameMem = null;
    private SurfaceHolder mSurface;
    private Canvas gameCanvas;
    EntityHolder entities;
    //playableCharacter memCharacter;
    Paint memPaint;
    NoiseMaker noiseMaker;
    //Bitmap bgTile;
    protected final int mSX;
    protected final int mSY;
    private int memFontSize;
    private int memFontMargin;
    protected long framesPerSecond;
    protected boolean bgMusicSwitch;
    private boolean drawOnceBoolean = false;
    protected boolean downPressed = false;
    private int[] savedPositionBeforeExit;
    private int animationOverlapCounter;
    private boolean enteredEscape;
    private boolean setSpawnAnotherEnemy;
    private boolean startNewGame;
    private boolean playerDead = false;
    public boolean doStop = false;
    private int startRunningFrom;
    public gameHandler(Context context, int pixelsHorisontal, int pixelsVertical, boolean musicSwitch) {
        super(context);
        bgMusicSwitch = musicSwitch;
        mSX = pixelsHorisontal;
        mSY = pixelsVertical;
        mSurface = getHolder();
        memPaint = new Paint();
        noiseMaker = new NoiseMaker();
        noiseMaker.setSoundPool(this.getContext(), 50);
        noiseMaker.loadAllSounds(this.getContext(), noiseMaker.songIds);
        Bitmap bgTile = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.test3);
        bgTile = Bitmap.createScaledBitmap(bgTile,mSX,mSY,true);//scaleBitmap(bgTile, mSX, mSY);
        entities = new EntityHolder(this.getContext(),mSX,mSY, bgTile);
        startGame();
        noiseMaker.bgMusic(this.getContext(), bgMusicSwitch);
        memFontMargin = mSX / 100 * 2;
        memFontSize = mSX / 100 * 6;
        entities.spawnInitial();
        Log.d("debugging", "___STARTAR NYTT SPEL___");
        Log.d("debugging", "entities: " + entities);
    }

    private void startGame() {
        resume();
    }

    protected void resume() {
        game_running = true;
        game_pause = false;
        threadGameMem = new Thread(this);
        threadGameMem.start();
    }
    private boolean deSpawnOnlyOnce = false;
    private long transitionTimer;
    @Override
    public void run() {
            while (game_running) {
                long timeFrameStart = System.currentTimeMillis();

                if (!game_pause) {
                    if (entities.hasEnteredEscape) {
                    pauseGame();
                    }
                    if (entities.checkHealth()){
                        pauseGame();
                        playerDead = true;
                    }
                    handleFrameSetup();
                    long timePerFrame = System.currentTimeMillis() - timeFrameStart;
                    if (timePerFrame > 0) {
                        framesPerSecond = 1000 / timePerFrame;
                    }
                } else{
                    paintBlack();
                    if (entities.hasEnteredEscape) {
                        if (!deSpawnOnlyOnce) {
                            transitionTimer = System.currentTimeMillis();
                            entities.deSpawnEverything();
                            deSpawnOnlyOnce = true;
                        }
                        newTransitionAnimationScreen(System.currentTimeMillis());

                    }else{
                        if (playerDead) {
                            //game_pause = true;
                            // game_running = false;
                            if ((System.currentTimeMillis()-transitionTimer) > 5000){
                                Log.d("debugging", "transitiontimer:" + (System.currentTimeMillis()-transitionTimer));
                                stopGame();
                            }
                        }else{
                            transitionTimer = System.currentTimeMillis();
                        }
                }}


            }

    }

    private void paintBlack(){
        if (mSurface.getSurface().isValid() && game_running) {
            gameCanvas = mSurface.lockCanvas();
            gameCanvas.drawColor(Color.BLACK);
            if (playerDead){
                deathMessage();
                game_pause = true;
            }
            mSurface.unlockCanvasAndPost(gameCanvas);
        }
    }
    private void newTransitionAnimationScreen(long timeElapsed){
       // memCharacter.movement();
       // paintBlack();
        // när gubben sprungit till andra sidan skärmen
        // new int[]{savedPositionBeforeExit,savedPositionBeforeExit[1]
        if (timeElapsed - transitionTimer > 400){//(memCharacter.objectPosition[0] == startRunningFrom){ //mSX-runner.objWidth-2
            entities.hasEnteredEscape = false;
            //deSpawnEverything();
            game_pause = false;
            deSpawnOnlyOnce = false;
            //spelet är igång igen
            //Stoppa in nya objekt att interagera med
            entities.spawnNewRoom(savedPositionBeforeExit);//ska vara inverterad X-position
        }
    }
    public void stopBgMusicEngine() {
        NoiseMaker.stopMusic();
    }

    protected void stopGame() {
        Log.d("debugging", "försöker stänga av ");
        game_pause = true;
        game_running = false;
        if (threadGameMem.isAlive())  {//
            try {
                //if (threadGameMem.isInterrupted()){}
                //Thread.currentThread().interrupt();
                Log.d("debugging", "threadGameMem.join ");
                threadGameMem.join(5000);
                threadGameMem.stop();
                Log.d("debugging", "efter join");
            } catch (InterruptedException e) {
                Log.e("Error", "Game Thread unable to join");
                e.printStackTrace();
            }

        }
    }
    private void nullifyAll(){
        gameCanvas = null;
        mSurface = null;

        //deSpawnEverything();
        //stopBgMusicEngine();
        noiseMaker = null;
        entities = null;
        memPaint = null;
        //threadGameMem = null;
    }

    public void pauseGame() {
        game_pause = true;
    }

    protected void handleFrameSetup() {
        if (setSpawnAnotherEnemy) {
            entities.spawnEnemy();
            setSpawnAnotherEnemy = false;
        }
        if (mSurface.getSurface().isValid()) {
            gameCanvas = mSurface.lockCanvas();
           // if (!game_pause){}    else{}
                entities.checkFigureMovement();
                entities.drawTheseObjectsAndThings(gameCanvas);
                entities.checkCollisions(noiseMaker, savedPositionBeforeExit, enteredEscape);
                writeText();


              // else{ }
                  //  gameCanvas.drawColor(Color.BLACK);


            mSurface.unlockCanvasAndPost(gameCanvas);
        }
    }

    protected void deathMessage(){
        String wasItEnough = entities.guldpengar > 1000 ? "till" : "inte till";
        String str = "Modige Morgan samlade";
        String str1 = "totalt ihop " + entities.guldpengar + " mynt";
        String str2 = "Det räckte " + wasItEnough;
        String str3 =  "sjukhuskostnaderna";
        float rectH = (memFontMargin+memFontSize)*2;
        memPaint.setColor(Color.BLACK);
        float widthAdjust = 50;
        gameCanvas.drawRect(0+widthAdjust,mSY/2-rectH, mSX-widthAdjust,mSY/2+3*(memFontSize+memFontMargin),memPaint);
        memPaint.setColor(Color.WHITE);
        memPaint.setTextSize(memFontSize);
        float textWidth = memPaint.measureText(str);
        float xPos = (mSX-textWidth)/2;
        float yPos = mSY/2-memFontSize;//-rectH/2 + memFontSize*2;
        gameCanvas.drawText(str, xPos, yPos, memPaint);
        gameCanvas.drawText(str1, xPos, yPos+memFontSize+memFontMargin, memPaint);
        gameCanvas.drawText(str2, xPos, yPos+2*(memFontSize+memFontMargin), memPaint);
        gameCanvas.drawText(str3, xPos, yPos+3*(memFontSize+memFontMargin), memPaint);
    }
    protected void writeText() {
        //
        //int backgroundColor = Color.BLACK;
        memPaint.setColor(Color.BLACK);
        float rectHeight = (memFontSize + memFontMargin) * 2;
        gameCanvas.drawRect(0, mSY - rectHeight, mSX, mSY + 75, memPaint);
        memPaint.setColor(Color.WHITE);
        memPaint.setTextSize(memFontSize); //" FPS: " + framesPerSecond +
        String writeString = ("Guldpengar: " + entities.guldpengar + " Hälsa: " + entities.memCharacter.health);
        float textWidth = memPaint.measureText(writeString);
        float xPosition = (mSX - textWidth) / 2; // Centrera texten horisontellt
        float yPosition = mSY - rectHeight / 2 + memFontSize / 3; // Centrera texten vertikalt
        gameCanvas.drawText(writeString, xPosition, yPosition, memPaint);
        //gameCanvas.drawText(writeString, memFontMargin, memFontSize, memPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //downPressed = false;
        //if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == 1 && MotionEvent.ACTION_BUTTON_PRESS == 11){
        //if (memCharacter != null) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                int tX = (int) motionEvent.getX();
                int tY = (int) motionEvent.getY();
                entities.memCharacter.setNewObjectPosition(new int[]{tX, tY});
                downPressed = true;
                break;

            case MotionEvent.ACTION_UP:
                downPressed = false;
                break;

            case MotionEvent.ACTION_MOVE:
                entities.memCharacter.setNewObjectPosition(new int[]{(int) motionEvent.getX(), (int) motionEvent.getY()});
                break;
        }//}
        return true;
    }
}

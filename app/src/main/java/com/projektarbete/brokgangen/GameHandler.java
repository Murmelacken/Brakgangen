package com.projektarbete.brokgangen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameHandler extends SurfaceView implements Runnable {
    private boolean game_running = false;
    private boolean game_pause = true;
    public boolean STOPTHEGAME = false;
    private Thread threadGameMem = null;
    private SurfaceHolder mSurface;
    private Canvas gameCanvas;
    EntityHolder entities;
    Paint memPaint;
    NoiseMaker noiseMaker;
    protected final int mSX, mSY;
    private int memFontSize;
    private int memFontMargin;
    protected long framesPerSecond;
    protected boolean bgMusicSwitch;
    protected boolean downPressed = false;
    private boolean playerDead = false;
    public GameHandler(Context context, int pixelsHorisontal, int pixelsVertical, boolean musicSwitch) {
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
        memFontMargin = mSX / 100 * 2; //fontmargin: 2%
        memFontSize = mSX / 100 * 6;   //fontSize:   6%
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
                //om ej pausat
                if (!game_pause) {
                    //om spelaren är död, sätt variabel till true
                    playerDead = entities.checkHealth();
                    // kollar för vilka värden jag vill pausa spelet
                    if (entities.hasEnteredEscape || playerDead) {
                        pauseGame();
                        transitionTimer = System.currentTimeMillis();
                        //
                        //break;
                    }
                    //kör spelet som vanligt
                    handleFrameSetup();
                }
                //om spelet är pausat
                else{
                    //rita bakgrunden och skriv lite text
                    if (mSurface.getSurface() != null){
                    paintBlack();}

                    // om spelaren går genom tunnel
                    if (entities.hasEnteredEscape) {
                        // ta bort alla gamla instanser och fortsätt...
                        if (!deSpawnOnlyOnce) {
                            transitionTimer = System.currentTimeMillis();
                            entities.deSpawnEverything();
                            deSpawnOnlyOnce = true;
                        }
                        // vänta och kör igång spelet igen med nya objekt
                        newTransitionAnimationScreen(System.currentTimeMillis());
                    }else{
                    //Spelaren har dött dvs health < 0
                        if (playerDead) {
                            //game_pause = true;
                            //game_running = false;
                            // vänta minst 5 sekunder så man får njuta av dödsskärmen
                            if ((System.currentTimeMillis()-transitionTimer) > 5000){
                                Log.d("debugging", "transitiontimer:" + (System.currentTimeMillis()-transitionTimer));
                                STOPTHEGAME = true;
                                //avsluta
                                //nullifyAll();
                                stopGame();
                            }
                        }else{
                            transitionTimer = System.currentTimeMillis();
                        }
                    }}


                long timePerFrame = System.currentTimeMillis() - timeFrameStart;
                if (timePerFrame > 0) {
                    framesPerSecond = 1000 / timePerFrame;
                }
            }
//       this.getContext().startActivity(new Intent(getContext(), MainMenu.class));
        threadGameMem.interrupt();
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
        if (timeElapsed - transitionTimer > 400){//(memCharacter.objectPosition[0] == startRunningFrom){ //mSX-runner.objWidth-2
            entities.hasEnteredEscape = false;
            //deSpawnEverything();
            game_pause = false;
            deSpawnOnlyOnce = false;
            //spelet är igång igen
            //Stoppa in nya objekt att interagera med
            entities.spawnNewRoom(entities.charPositionBefore);//ska vara inverterad X-position
        }
    }
    public void stopBgMusicEngine() {
        NoiseMaker.stopMusic();
    }

    protected void stopGame() {
        Log.d("debugging", "försöker stänga av ");
        game_running = false;
        game_pause = true;
       ;
        if (threadGameMem.isAlive())  {//
                try {
                    //if (threadGameMem.isInterrupted()){}
                    //Thread.currentThread().interrupt();
                    threadGameMem.interrupt();

                    threadGameMem.join(5000);

                    Log.d("debugging", "efter join");
                } catch (InterruptedException e) {
                    Log.e("Error", "Game Thread unable to join");
                    e.printStackTrace();
                } finally {
                    if (threadGameMem.isAlive()){
                        Log.d("debugging", "threadGameMem.isdaemon " + threadGameMem.isDaemon());
                        threadGameMem.stop();
                        //nullifyAll();
                }}
        }
    }
    private void nullifyAll(){
        gameCanvas = null;
        mSurface = null;
        stopBgMusicEngine();
        noiseMaker = null;
        entities = null;
        memPaint = null;
        threadGameMem = null;
    }

    public void pauseGame() {
        game_pause = true;
    }

    protected void handleFrameSetup() {
        if (entities.setSpawnAnotherEnemy) {
            entities.spawnEnemy();
            entities.setSpawnAnotherEnemy = false;
        }
        if (mSurface.getSurface().isValid()) {
            gameCanvas = mSurface.lockCanvas();
           // if (!game_pause){}    else{}
                entities.checkFigureMovement();
                entities.drawTheseObjectsAndThings(gameCanvas);
                //entities.checkCollisions(noiseMaker, savedPositionBeforeExit, enteredEscape);
                entities.newCollisionCheck(noiseMaker);
                //entities.checkOutOfMapBoundaries(entities.memCharacter.objectCBox);
            writeText();
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
        if (game_running) {
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
            }//}}
    }
        return true;
    }
}

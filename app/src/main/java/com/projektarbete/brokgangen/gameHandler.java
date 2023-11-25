package com.projektarbete.brokgangen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class gameHandler extends SurfaceView implements Runnable {
    private boolean game_running = false;
    private boolean game_pause = true;
    private Thread threadGameMem = null;
    private final SurfaceHolder mSurface;
    private Canvas gameCanvas;
    Paint memPaint;
    protected final int mSX;
    protected final int mSY;
    private int memFontSize;
    private int memFontMargin;
    protected long framesPerSecond;
    protected boolean bgMusicSwitch;
    private boolean drawOnceBoolean = false;
    private int clickScore;
    protected boolean downPressed = false;
    protected playableCharacter memCharacter;
    protected playableCharacter runner;
    public static int antalPengar = 10;
    public static int antalTunnor = 15;
    NoiseMaker noiseMaker;
    Bitmap bgTile;
    private NPC enemy;
    private List<DrawObject> objectsToDraw = new ArrayList<>();
    public ArrayList<entity> entities = new ArrayList<>();
    private int[] savedPositionBeforeExit;
    private int animationOverlapCounter;
    private boolean enteredEscape;
    private boolean setSpawnAnotherEnemy;
    private boolean createNewRoom;
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
        bgTile = BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.test3);
        bgTile = scaleBitmap(bgTile, mSX, mSY);
        startGame();
        noiseMaker.bgMusic(this.getContext(), bgMusicSwitch);
        memFontMargin = mSX / 100 * 2;
        memFontSize = mSX / 100 * 6;
        spawnInitial();
        Log.d("debugging", "___STARTAR NYTT SPEL___");
        Log.d("debugging", "entities: " + entities);
    }

    public static Bitmap scaleBitmap(Bitmap bp, int screenWidth, int screenHeight) {
        int originalWidth = bp.getWidth();
        int originalHeight = bp.getHeight();

        float scaleWidth = (float) screenWidth / originalWidth;
        float scaleHeight = (float) screenHeight / originalHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bp, 0, 0, originalWidth, originalHeight, matrix, false);
    }

    private int[] newRandomPosition(int abX, int abY, int baX, int baY) {
        int[] position = new int[2];
        int mSXL = abX; // justera så inte figur kan gå utanför skärm typ
        int mSYL = abY;
        int highLimitX = baX;
        int highLimitY = baY;
        int mSXH = mSX - highLimitX;
        int mSYH = mSY - highLimitY;
        position[0] = (int) (mSXL + (Math.random() * (mSXH - mSXL)));
        position[1] = (int) (mSYL + (Math.random() * (mSYH - mSYL)));
        return position;
    }

    private void spawnInitial() {
        memCharacter = new playableCharacter(this.getContext(), mSX, mSY, new int[]{mSX / 2, 0});
        entities.add(memCharacter);
        spawnCorners();
        enemy = new NPC(this.getContext(), mSX, mSY, newRandomPosition(0, mSY / 4, mSX / 3, mSY / 4));
        entities.add(enemy);
        spawnPassage(0);
        spawnPassage(1);
        spawnMynts(antalPengar);
        spawnBarrels(antalTunnor);
        spawnEscape(0);
    }
    private void spawnCorners(){
        for (int i = 0; i < 4; i++) {
            cornerFigure corner = new cornerFigure(this.getContext(), mSX, mSY, new int[]{0, 0}, i);
            entities.add(corner);
        }
    }
    private void spawnNewRoom(int[] placement){
        //if (memCharacter != null){}
        //memCharacter = null;
        memCharacter = new playableCharacter(this.getContext(), mSX, mSY, placement);
        entities.add(memCharacter);
        spawnCorners();
        spawnPassage(1);
        spawnMynts((int) (Math.random() * 5)+5);
        spawnBarrels((int) (Math.random() * 10)+5);
        spawnEnemy();
        spawnEscape(1);
    }
    private void spawnEscape(int side){
        entities.add(new escapeTunnel(this.getContext(),mSX,mSY,new int[]{0,0},side));
        //antingen 0 (vänster) eller 1 (höger)
    }
    private void spawnEnemy(){
        entity temp = new NPC(this.getContext(), mSX, mSY, newRandomPosition(0, mSY / 4, mSX / 3, mSY / 4));
        entities.add(temp);
    }
    private void deSpawnEverything(){
        entities.clear();
        memCharacter = null;
        enemy = null;

    }

    private void spawnPassage(int i){
        entities.add(new passageWay(this.getContext(),mSX,mSY,new int[]{mSX/3,0},i));
        entities.add(new passageWay(this.getContext(),mSX,mSY,new int[]{mSX/3,0},i));

    }
    private void spawnMynts(int antalPengar) {
        for (int i = 0; i < antalPengar; i++) {
            entities.add(new myntObjekt(this.getContext(), mSX, mSY, newRandomPosition(0, mSY / 4, mSX / 3, mSY / 4)));

        }
    }

    private void spawnBarrels(int antalTunnor) {
        for (int i = 0; i < antalTunnor; i++) {
            entities.add(new barrel(this.getContext(), mSX, mSY, newRandomPosition(0, mSY / 4, mSX / 3, mSY / 4)));
        }

    }

    protected void setScore(int passVar) {
        clickScore += passVar;
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

    @Override
    public void run() {
        while (game_running) {
            long timeFrameStart = System.currentTimeMillis();
            if (!game_pause) {
                if (downPressed) {
                    //something
                }

                handleFrameSetup();

            }else{
                if (enteredEscape){
                    deSpawnEverything();
                    enteredEscape = false;
                }
                transitionAnimationScreen();
            }
            long timePerFrame = System.currentTimeMillis() - timeFrameStart;
            if (timePerFrame > 0) {
                framesPerSecond = 1000 / (timePerFrame);
            }
        }
    }

    private void transitionAnimationScreen(){
        //TODO
        int[] reachThisDestination = savedPositionBeforeExit;
        if (runner == null){ //setup
            //lite meningslöst kanske men skapar en ny runner till animationen
            //sedan hämtar jag enbart objwidth och stoppar ut runner på rätt plats
            runner = new playableCharacter(this.getContext(),mSX,mSY,new int[]{0,0});

           // reachThisDestination[0] = reachThisDestination[0] > mSX/2 ? mSX-runner.objWidth : runner.objWidth;
            if (savedPositionBeforeExit[0] > mSX/2 ){
                //går in på höger skärmsida
                //
                reachThisDestination[0] = mSX-runner.objWidth-25;
                startRunningFrom = runner.objWidth+25;
            }else{
                //går in på vänster sida
                reachThisDestination[0] = runner.objWidth+25;
                startRunningFrom = mSX-runner.objWidth-25;

            }
            //savedPositionBeforeExit = reachThisDestination;
            //runner = null;
            runner = new playableCharacter(this.getContext(),mSX,mSY,reachThisDestination);
            runner.setNewObjectPosition(new int[]{startRunningFrom,savedPositionBeforeExit[1]});
            runner.changeSpeed(10.0f);
        }
            //Först förflyttar vi gubben
            //runner.runAcross((otherSideX-savedPositionBeforeExit[0])/(10));
            runner.movement();
            //speed är ej fart utan hastighet dvs riktad vektor
            //(före - efter) / (antal) så felet uppdelat i antal rutor
            if (mSurface.getSurface().isValid() && game_pause) {
                gameCanvas = mSurface.lockCanvas();
                gameCanvas.drawColor(Color.BLACK);
                runner.getCurrentStateBitmap();
                mSurface.unlockCanvasAndPost(gameCanvas);
                Log.d("animation","gubben runner:  " + runner);
                Log.d("animation","lite värden: " + Arrays.toString(savedPositionBeforeExit) + " " + Arrays.toString(reachThisDestination));
            }

            // när gubben sprungit till andra sidan skärmen
        // new int[]{savedPositionBeforeExit,savedPositionBeforeExit[1]
        if (runner.objectPosition[0] == savedPositionBeforeExit[0]){ //mSX-runner.objWidth-2
            deSpawnEverything();
            game_pause = false;
            //spelet är igång igen
            //Stoppa in nya objekt att interagera med
            spawnNewRoom(savedPositionBeforeExit);//ska vara inverterad X-position
        }

    }
    public void stopBgMusicEngine() {
        NoiseMaker.stopMusic();
    }

    protected void stopGame() {
        game_pause = true;
        game_running = false;
        // SurfaceHolder.Callback.surfaceDestroyed(mSurface);
        try {
            threadGameMem.join();
        } catch (InterruptedException e) {
            Log.e("Error", "Game Thread joined");
        }

    }
    private void checkHealth(){
        if (memCharacter.health < 0){
            //memCharacter = null;
            entities.remove(memCharacter);
            //memCharacter = null;
            pauseGame();
            //memCharacter.remove();
        }
    }
    public void pauseGame() {
        game_pause = true;
    }

    protected void handleFrameSetup() {
        if (mSurface.getSurface().isValid() && !game_pause) {
            gameCanvas = mSurface.lockCanvas();
            checkFigureMovement();
            drawTheseObjectsAndThings();
            checkCollisions();
            checkHealth();
            writeText();
            if (setSpawnAnotherEnemy){
                spawnEnemy();
                setSpawnAnotherEnemy = false;
            }
            mSurface.unlockCanvasAndPost(gameCanvas);
        }
    }

    private void checkFigureMovement() {
        memCharacter.movement();
        for (entity en : entities){
            if (en instanceof NPC){
                ((NPC) en).movement(memCharacter.getObjectPosition());
            }
        }
    }

    private void checkCollisions() {
        //RectF pCRectF = memCharacter.getObjectCBox();
        checkOutOfMapBoundaries(memCharacter.getObjectCBox());
        // Iterera över och hantera både rörliga och orörliga entiteter
        for (entity en : entities) {
            //en.draw(gameCanvas);
            RectF a = en.getObjectCBox();
            if (a == null && !(en == null)) {
            } else {
                if (memCharacter.getObjectCBox().intersects(a.left, a.top, a.right, a.bottom)) {
                    if (en instanceof cornerFigure || en instanceof barrel) {
                        //immovableEntity immovableObj = (immovableEntity) en;// om man behöver specifika funktioner genom heritance
                        memCharacter.onCollision(en.getObjectCBox());
                        noiseMaker.playImmovable();
                    }
                    else if (en instanceof myntObjekt) {
                        noiseMaker.playPling();
                        setScore(1);
                        if (clickScore % 3 == 0){
                            setSpawnAnotherEnemy = true;
                        }
                        ((myntObjekt) en).remove();
                    }
                    else if (en instanceof NPC) {
                        memCharacter.onCollision(en.getObjectCBox());
                        memCharacter.damage(1);
                        //Om NPC kolliderar med annan npc... hur blir detta..
                        // if (en.intersects(en.getObjectCBox(),))
                    }
                    else if (en instanceof escapeTunnel){
                        //immovableEntity immovableObj = (immovableEntity) en;
                        boolean underLimit = memCharacter.objectPosition[1] > a.bottom;
                        //memCharacter.objectPosition[1] > immovableObj.objectPosition[1]+immovableObj.objHeight;
                        boolean overLimit = memCharacter.objectPosition[1] > a.top;//immovableObj.objectPosition[1];
                        Log.d("escape", "underlimit: " + underLimit + " over: " + overLimit);
                        if (!underLimit && overLimit){
                            //karaktären går in i tunneln
                            savedPositionBeforeExit = memCharacter.objectPosition;
                            game_pause = true;
                            enteredEscape = true;
                            //deSpawnEverything();
                            //Svart skärm - gubben springer åt motsatt håll
                            //initalSpawn();
                        }else {
                            memCharacter.onCollision(a);
                        }
                    }
                }
            }
        }
        //Log.d("debugging", "tog stopp");
    }

    private void checkOutOfMapBoundaries(RectF player) {
        if (player.right >= mSX) {
            memCharacter.updateObjectPosition(-10,0);
            //memCharacter.setNewObjectPosition(passPos);
            //return true;
            //karaktären är till höger på kartan
        }
        if (player.left <= 1) {
            memCharacter.updateObjectPosition(10,0);
            //return true;
            //karaktären är till vänster på kartan
        }
        if (player.bottom >= mSY - 1) {
            memCharacter.updateObjectPosition(0,-10);
            //return true;
            //karaktären är i nere på kartan
        }
        if (player.top <= 1) {
            memCharacter.updateObjectPosition(0,10);
            //return true;
        }
        //return false;
    }

    public void drawTheseObjectsAndThings() {
        gameCanvas.drawColor(Color.argb(255, 112, 84, 72));
        objectsToDraw.add(new DrawObject(bgTile, new RectF(0, 0, mSX, mSY)));
        for (entity ent : entities) {
            RectF placeAt = ent.getObjectCBox();
            Bitmap figure = ent.getCurrentStateBitmap();
            if (figure == null || placeAt == null) {
                Log.d("debugging", "följande entity.bitmap är null: " + ent);
            } else {
                objectsToDraw.add(new DrawObject(figure, placeAt));
            }
        }
        drawBatchedObjects();
    }


    private void drawBatchedObjects() {
        // Itererar genom ArrayList med objekt att rita
        for (DrawObject drawObject : objectsToDraw) {
            gameCanvas.drawBitmap(drawObject.bitmap, null, drawObject.rect, null);
        }
        // Rensa listan efter att alla objekt är ritade
        objectsToDraw.clear();
    }

    private static class DrawObject {
        Bitmap bitmap;
        RectF rect;
        DrawObject(Bitmap bitmap, RectF rect) {
            this.bitmap = bitmap;
            this.rect = rect;
        }
    }

    protected void writeText() {
        //
        //int backgroundColor = Color.BLACK;
        memPaint.setColor(Color.BLACK);
        float rectHeight = (memFontSize + memFontMargin) * 2;
        gameCanvas.drawRect(0, mSY - rectHeight, mSX, mSY + 75, memPaint);
        memPaint.setColor(Color.WHITE);
        memPaint.setTextSize(memFontSize); //" FPS: " + framesPerSecond +
        String writeString = ("Score: " + clickScore + " Health: " + memCharacter.health);
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
        if (memCharacter != null) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (game_pause){
                    game_pause = false;
                    createNewRoom = true;
                }
                int tX = (int) motionEvent.getX();
                int tY = (int) motionEvent.getY();
                memCharacter.setNewObjectPosition(new int[]{tX, tY});
                downPressed = true;
                break;

            case MotionEvent.ACTION_UP:
                downPressed = false;
                break;

            case MotionEvent.ACTION_MOVE:

                memCharacter.setNewObjectPosition(new int[]{(int) motionEvent.getX(), (int) motionEvent.getY()});

                break;
        }}
        return true;
    }
}

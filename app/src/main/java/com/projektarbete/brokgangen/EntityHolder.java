package com.projektarbete.brokgangen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EntityHolder extends Activity {
    public ArrayList<entity> entities = new ArrayList<>();
    public ArrayList<movableEntity> enemies = new ArrayList<>();
    public ArrayList<immovableEntity> immovables = new ArrayList<>();
    public playableCharacter memCharacter;
    public static int antalPengar = 10;
    public static int antalTunnor = 15;
    public boolean setSpawnAnotherEnemy;
    public int[] charPositionBefore;
    private Bitmap background;
    public int guldpengar;
    public boolean hasEnteredEscape;
    int mSX, mSY;
    Context c;
    private int spawnEscapeTunnelOnSide = 0;
    private List<DrawObject> objectsToDraw = new ArrayList<>();
    public EntityHolder(Context context, int sX, int sY, Bitmap bg){
        c=context;
        background = bg;
        mSX = sX; mSY = sY;
    }

    public boolean checkHealth(){
        return entities.contains(memCharacter) && memCharacter.health < 0;
    }

    /*void checkCollisions(NoiseMaker noiseMaker, int[] savedPosition, boolean enteredEscape) {
        ArrayList<NPC> enemies = new ArrayList<>();

        //RectF pCRectF = memCharacter.getObjectCBox();
        checkOutOfMapBoundaries(memCharacter.getObjectCBox());
        // Iterera över och hantera både rörliga och orörliga entiteter

        for (entity en : entities) {
            //en.draw(gameCanvas);
            RectF a = en.getObjectCBox();
            if (a != null) {
            //} else {
                if (RectF.intersects(memCharacter.getObjectCBox(),a)){// (memCharacter.getObjectCBox().intersects(a.left, a.top, a.right, a.bottom)) {

                    if (en instanceof cornerFigure) {
                        //immovableEntity immovableObj = (immovableEntity) en;// om man behöver specifika funktioner genom heritance
                        memCharacter.onCollision(en.getObjectCBox());
                        noiseMaker.playImmovable();
                    }else if (en instanceof barrel){
                        //RectF pC = memCharacter.getObjectCBox();
                        if (memCharacter.getObjectCBox().bottom > en.getObjectCBox().bottom-en.objHeight*0.33){
                            memCharacter.onCollision(en.getObjectCBox());
                            noiseMaker.playImmovable();
                        }
                    }
                    else if (en instanceof myntObjekt) {
                        noiseMaker.playPling();
                        guldpengar+=1;
                        if (guldpengar % 3 == 0){
                            setSpawnAnotherEnemy = true;
                        }
                        ((myntObjekt) en).remove();
                    }
                    else if (en instanceof NPC) {
                        memCharacter.onCollision(en.getObjectCBox());
                        memCharacter.damage(1);
                        //noiseMaker.playDamaged();
                        //Om NPC kolliderar med annan npc... hur blir detta..
                        // if (en.intersects(en.getObjectCBox(),))
                    }
                    else if (en instanceof escapeTunnel){
                        //immovableEntity immovableObj = (immovableEntity) en;
                        boolean underLimit = memCharacter.objectPosition[1] > a.bottom;
                        //memCharacter.objectPosition[1] > immovableObj.objectPosition[1]+immovableObj.objHeight;
                        boolean overLimit = memCharacter.objectPosition[1] > a.top;//immovableObj.objectPosition[1];
                        //
                        if (!underLimit && overLimit){
                            //karaktären går in i tunneln
                            charPositionBefore = memCharacter.objectPosition;
                            //pauseGame();
                            hasEnteredEscape = true;
                            //enteredEscape = true;
                            //Svart skärm - gubben springer åt motsatt håll
                            Log.d("escape", "underlimit: " + underLimit + " over: " + overLimit);
                        }else {
                            memCharacter.onCollision(a);
                        }
                    }
                }
            }
        }
        //Log.d("debugging", "tog stopp");
    }*/
    public void newCollisionCheck(NoiseMaker noiseMaker){
        checkOutOfMapBoundaries(memCharacter.getObjectCBox());
        for (int i = 0; i < entities.size(); i++){
            entity a = entities.get(i);
            boolean playerCheck =  a instanceof playableCharacter;
            RectF aBox = a.getObjectCBox();
            if (!(a instanceof immovableEntity)){
                for (int j = i+1; j < entities.size(); j++){
                    entity b = entities.get(j);
                    RectF bBox = b.getObjectCBox();
                    //om dessa ting kolliderar...
                    if (RectF.intersects(aBox, bBox)) {
                        //om kollision av a sker mot orörligt ting...
                        if (b instanceof immovableEntity){
                            // soundtrig innebär spelarkaraktärens kollision
                            // då gör vi ljud
                            if (b instanceof barrel) {
                                //sätter rektangeln ned mot botten av tunnnan
                                if (aBox.bottom > bBox.bottom - b.objHeight * 0.33) {
                                    if (playerCheck){noiseMaker.playImmovable();}
                                    a.onCollision(bBox);
                                }
                                //
                                }else if (b instanceof myntObjekt){
                                    guldpengar+=1;
                                    if (playerCheck){noiseMaker.playPling();}
                                    if (guldpengar % 3 == 0){
                                        setSpawnAnotherEnemy = true;
                                    }
                                    ((myntObjekt) b).remove();
                                } else if (b instanceof cornerFigure){
                                    if (playerCheck){noiseMaker.playImmovable();}
                                    //immovableEntity immovableObj = (immovableEntity) en;// om man behöver specifika funktioner genom heritance
                                    a.onCollision(bBox);
                            }else if (b instanceof escapeTunnel){
                                if (playerCheck){
                                    //immovableEntity immovableObj = (immovableEntity) en;
                                    boolean underLimit = memCharacter.objectPosition[1] < aBox.bottom;
                                    //memCharacter.objectPosition[1] > immovableObj.objectPosition[1]+immovableObj.objHeight;
                                    boolean overLimit = memCharacter.objectPosition[1] > aBox.top;//immovableObj.objectPosition[1];
                                    Log.d("escape", "underlimit: " + underLimit + " over: " + overLimit);

                                    if (underLimit && !overLimit){
                                        //karaktären går in i tunneln
                                       // Log.d("escape", "underlimit: " + underLimit + " over: " + overLimit);
                                        noiseMaker.playEscapeSound();
                                        charPositionBefore = memCharacter.objectPosition;
                                        //pauseGame();
                                        hasEnteredEscape = true;
                                        //enteredEscape = true;
                                    }else {
                                        a.onCollision(bBox);
                                }
                                }else {
                                    a.onCollision(bBox);}
                            }
                        }
                            //om kollision med rörligt ting
                            else{
                                //har bara lagt in fienden NPC än så länge
                                if (b instanceof NPC){
                                    if (playerCheck){noiseMaker.playHit();}
                                    a.onCollision(bBox);

                                }
                            }
                    }
                }
            }
        }
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
    public void drawTheseObjectsAndThings(Canvas gameCanvas) {
        gameCanvas.drawColor(Color.argb(255, 112, 84, 72));
        objectsToDraw.add(new DrawObject(background, new RectF(0, 0, mSX, mSY)));
        //ArrayList<entity> tempities = entities.getEntityList();
        for (entity ent : entities) {
            RectF placeAt = ent.getObjectCBox();
            Bitmap figure = ent.getCurrentStateBitmap();
            if (figure == null || placeAt == null) {
                Log.d("debugging", "följande entity.bitmap är null: " + ent);
            } else {
                objectsToDraw.add(new DrawObject(figure, placeAt));
            }
        }
        drawBatchedObjects(gameCanvas);
    }
    private void drawBatchedObjects(Canvas gameCanvas) {
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
    private int getAndSetEscapeTunnel(){
        spawnEscapeTunnelOnSide = spawnEscapeTunnelOnSide == 0 ? 1 : 0;
        return spawnEscapeTunnelOnSide;
    }

    public void spawnInitial() {
        spawnPlayer();
        spawnCorners();
        spawnEnemy();
        spawnPassage(0);
        spawnPassage(1);
        spawnMynts(antalPengar);
        spawnBarrels(antalTunnor);
        spawnEscape();
    }
    public void checkFigureMovement() {
        memCharacter.movement();
        for (entity en : entities){
            if (en instanceof NPC){
                ((NPC) en).movement(memCharacter.getObjectPosition());
            }
        }
    }
    public ArrayList<entity> getEntityList(){
        return entities;
    }

    public void spawnPlayer(){
        memCharacter = new playableCharacter(c, mSX, mSY, new int[]{mSX / 2, 0});
        entities.add(memCharacter);
    }
    public void spawnCorners(){
        for (int i = 0; i < 4; i++) {
            cornerFigure corner = new cornerFigure(c, mSX, mSY, new int[]{0, 0}, i);
            entities.add(corner);
        }
    }
    public void spawnNewRoom(int[] placement){
        //if (memCharacter != null){}
        if (!entities.contains(memCharacter) && memCharacter != null){
            spawnPlayer();
            memCharacter.setObjectPosition(placement);
        }
        //new playableCharacter(this.getContext(), mSX, mSY, placement);
        spawnCorners();
        spawnPassage(0);
        spawnPassage(1);
        spawnMynts((int) (Math.random() * 5)+5);
        spawnBarrels((int) (Math.random() * 10)+5);
        spawnEnemy();
        spawnEscape();
    }
    public void spawnEscape(){
        entities.add(new escapeTunnel(c,mSX,mSY,new int[]{0,0},getAndSetEscapeTunnel()));
        //antingen 0 (vänster) eller 1 (höger)
    }
    void spawnEnemy(){
        entity temp = new NPC(c, mSX, mSY, newRandomPosition(0, mSY / 4, mSX / 3, mSY / 4));
        entities.add(temp);
    }
    public playableCharacter getMemChar(){
        return memCharacter;
    }
    void deSpawnEverything(){
        entities.clear();
        entities.add(memCharacter);
    }
    public void deSpawnEnemies(){
        entities.removeIf(ent -> ent instanceof NPC);
    }
    private void spawnPassage(int i){
        entities.add(new passageWay(c,mSX,mSY,new int[]{mSX/3,0},i));
        entities.add(new passageWay(c,mSX,mSY,new int[]{mSX/3,0},i));

    }
    public boolean checkIfContainsEntity(entity ent){
        //boolean send =
        return entities.contains(ent);
    }
    private void spawnMynts(int antalPengar) {
        for (int i = 0; i < antalPengar*2; i++) {
            entities.add(new myntObjekt(c, mSX, mSY, newRandomPosition(0, mSY / 4, mSX / 3, mSY / 4)));

        }
    }
    private void spawnBarrels(int antalTunnor) {
        for (int i = 0; i < antalTunnor; i++) {
            entities.add(new barrel(c, mSX, mSY, newRandomPosition(0, mSY / 4, mSX / 3, mSY / 4)));
        }

    }
}

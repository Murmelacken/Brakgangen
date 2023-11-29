package com.projektarbete.brokgangen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

public class gameBrakgangen extends AppCompatActivity {
    gameHandler runningGame;
    public Intent intent_mainmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_brakgangen);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            //int systemBarsBehavior = controller.getSystemBarsBehavior();
            if (false) {
            } else {
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                );
                //dölj statusrad
                controller.hide(WindowInsets.Type.statusBars());
                controller.hide(WindowInsets.Type.navigationBars());
                //controller.show(WindowInsets.Type.systemBars());
            }
        }
        runningGame = runGame();

    }
    protected gameHandler runGame(){
        DisplayMetrics disp = getResources().getDisplayMetrics();
        gameHandler myGame = new gameHandler(this, disp.widthPixels, disp.heightPixels, MainMenu.checkSwitch());
        setContentView(myGame);
        return myGame;
    }
    @Override
    public void onPause(){
        super.onPause();
        if (runningGame.noiseMaker != null && runningGame.bgMusicSwitch){
            runningGame.stopBgMusicEngine();
        }
        intent_mainmenu = new Intent(this, MainMenu.class);
        startActivity(intent_mainmenu);
        runningGame.pauseGame();
    }
    @Override
    public void onStop() {
        super.onStop();
        // runningGame.cancelCanvas();
        runningGame.stopGame();
       // if (runningGame != null){
       //     runningGame = null;
       // }
        finish();
        intent_mainmenu = new Intent(this, MainMenu.class);
        startActivity(intent_mainmenu);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        intent_mainmenu = new Intent(this, MainMenu.class);
        startActivity(intent_mainmenu);
        finish();
    }

}
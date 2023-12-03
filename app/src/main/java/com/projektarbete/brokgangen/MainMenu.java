package com.projektarbete.brokgangen;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {
    Button button_playGame;
    Button button_aboutGame;
    Button button_musicSwitch;
    public Intent intent_playGame;
    public Intent intent_aboutGame;
    public static boolean musicSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            //int systemBarsBehavior = controller.getSystemBarsBehavior();
            if (false) {
            } else {
                //dölj statusrad
                controller.hide(WindowInsets.Type.statusBars());
                controller.hide(WindowInsets.Type.navigationBars());
                //controller.show(WindowInsets.Type.systemBars());
            }
        }
        button_aboutGame = findViewById(R.id.btnAboutGame);
        button_playGame = findViewById(R.id.btnPlay);
        button_musicSwitch = findViewById(R.id.checkbox_music);
        intent_playGame = new Intent(MainMenu.this, GameBrakgangen.class);
        intent_aboutGame = new Intent(MainMenu.this, AboutTheGame.class);

        button_playGame.setOnClickListener(v -> startActivity(intent_playGame)); //startActivity(intent_playGame)
        button_aboutGame.setOnClickListener(v -> startActivity(intent_aboutGame));
        button_musicSwitch.setOnClickListener(v -> {
            if (musicSwitch == false){
                musicSwitch = true;
            }else{
                musicSwitch = false;
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static boolean checkSwitch(){
        return musicSwitch;
    }
}
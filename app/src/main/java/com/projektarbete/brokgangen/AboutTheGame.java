package com.projektarbete.brokgangen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

public class AboutTheGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_game);
        Toast.makeText(this, "Tryck på skärmen för att gå tillbaka", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        if (((motionEvent.getAction()) & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            //Intent backActivity = new Intent(about_the_game.this, MainActivity.class);
            //startActivity(backActivity); //detta återupptar inte MainActivity, utan skapar en ny mainActivity.
            //setContentView(MainActivity.class);
            //getOnBackPressedDispatcher();
            finish();
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }
}
package com.projektarbete.brokgangen;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoiseMaker {
    protected int[] songIds = {R.raw.ljudblaug, R.raw.bakgrundsljud, R.raw.lost_game, R.raw.touch_sound, R.raw.treehit, R.raw.test_promenadljud};
    private final String[] songNames = {"startLjud", "bgLjud","gameOver", "studs", "tree", "walk"};
    private final Map<String, Integer> soundList = new HashMap<>();
    protected List<Integer> loadedSoundIds = new ArrayList<>();
    static MediaPlayer myMediaPlayer = null;
    SoundPool mSP;
public NoiseMaker() {
}
    protected void setSoundPool(Context context, int max_streams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();
            mSP = new SoundPool.Builder()
                    .setMaxStreams(max_streams)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(max_streams, AudioManager.STREAM_MUSIC, 0);
        }

    }
public void playImmovable(){
    playSound("tree",0);
}
    public void playSound(String findString, int repeat){
        Integer soundId = soundList.get(findString);
        if (soundId > 0 ){
            mSP.play(soundId, 1, 1, 0, repeat, 1);

        }}
    public void bgMusic(Context context, boolean bgMusicSwitch){

        if (bgMusicSwitch){
            myMediaPlayer = MediaPlayer.create(context, R.raw.bakgrundsljud);
            myMediaPlayer.start();
            myMediaPlayer.setLooping(true);
        }
        else{
            stopMusic();
        }
    }
    public static void stopMusic(){
        if (!(myMediaPlayer == null)) {
            myMediaPlayer.release();
            myMediaPlayer = null;
        }

    }

    protected void loadAllSounds(Context context, int[] rawIds) {
        int i = 0;
        for (int element : rawIds) {
            //Log.d("debugging", "currently loadallsounds " + element);
            loadSounds(context, element, i);
            i++;
        }
        //Log.d("debugging", "soundList: " + soundList);
    }
    protected void loadSounds(Context context, int id, int something) {
        int currentId = mSP.load(context, id, 0);
        loadedSoundIds.add(currentId);
        soundList.put(songNames[something], currentId);
        //Log.d("debugging", "Loading song: " + something);
        mSP.setOnLoadCompleteListener((SoundPool soundPool, int sampleId, int status) -> {
            if (status == 0) {
                //Log.d("debugging", "sample loaded: " + sampleId);
            } else {
                // Log.d("debugging", "sample not loaded: " + sampleId);
            }
        });

    }

    public void playPling() {playSound("studs",0);
    }
}

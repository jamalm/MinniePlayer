package com.example.deadmadness.minnieplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

/**
 * Created by deadmadness on 11/12/15.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    //Media Player
    private MediaPlayer mediaPlayer;
    //list of songs
    private ArrayList<Song> songList;
    //position
    private int songPosition;
    //binder class instance
    private final IBinder musicBind = new MusicBinder();

    //song title
    private String songTitle = "";
    private static final int NOTIFY_ID=1;

    private boolean shuffle = false;
    private Random rand;


    public void onCreate() {
        //create service
        super.onCreate();
        //initialise the pos
        songPosition = 0;
        //create music player
        mediaPlayer = new MediaPlayer();
        //initialise the music player
        initMusicPlayer();

        //random number generator
        rand = new Random();
    }

    public void initMusicPlayer() {
        //set the player properties
        mediaPlayer.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setShuffle() {
        if(shuffle) {
            shuffle = false;
        } else {
            shuffle = true;
        }
    }

    public void setSongList(ArrayList<Song> songs) {
        songList = songs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return  musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mediaPlayer.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this,0,notIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification note = builder.build();

        startForeground(NOTIFY_ID, note);
    }

    public void setSong(int songIndex){
        songPosition = songIndex;
    }

    public void playSong() {
        //play a song

        //reset the player
        mediaPlayer.reset();

        //get a song
        Song playSong = songList.get(songPosition);
        songTitle = playSong.getTitle();
        //get id
        long currSong = playSong.getId();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", " Error setting the data source", e);
        }

        mediaPlayer.prepareAsync();
    }

    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer() {
        mediaPlayer.pause();
    }

    public void seek(int position) {
        mediaPlayer.seekTo(position);
    }

    public void begin() {
        mediaPlayer.start();
    }

    //next and previous functions below
    public void playPrev() {
        songPosition--;
        if (songPosition < 0) {
            songPosition = songList.size()-1;
        }
        playSong();
    }

    public void playNext() {
        if(shuffle) {
            int newSong = songPosition;
            while(newSong == songPosition) {
                newSong = rand.nextInt(songList.size());
            }
            songPosition = newSong;
        } else {
            songPosition++;
            if(songPosition >= songList.size()){
                songPosition = 0;
            }
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}

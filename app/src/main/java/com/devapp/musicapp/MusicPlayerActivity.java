package com.devapp.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvTime, tvDuration,tvTitle,tvArtist;
    SeekBar seekBarTime, seekBarVolume;
    ImageView ivPlay,ivPlaylist,ivPrevious,ivNext;
    MediaPlayer musicPlayer;
    Sensor sensor;
    SensorManager sensorManager;
    String duration;
    int position, durationInteger;
    Song song;
    ArrayList<Song> songArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extra = getIntent().getBundleExtra("extra");
        songArrayList = (ArrayList<Song>) extra.getSerializable("songs");
        song = (Song) getIntent().getSerializableExtra("song");
        position = (Integer) getIntent().getSerializableExtra("position");

        tvTime=findViewById(R.id.tvTime);
        tvDuration=findViewById(R.id.tvDuration);
        tvTitle=findViewById(R.id.tvTitle);
        tvArtist=findViewById(R.id.tvArtist);
        seekBarTime=findViewById(R.id.seekBarTime);
        seekBarVolume=findViewById(R.id.seekBarVolume);
        ivPlay=findViewById(R.id.ivPlay);
        ivPlaylist=findViewById(R.id.ivPlaylist);
        ivPrevious=findViewById(R.id.ivPrevious);
        ivNext=findViewById(R.id.ivNext);

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());

        musicPlayer = new MediaPlayer();
        musicPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //we initiate the media player with the music
        musicPlayer.setVolume(0.5f,0.5f);
        preparedMediaPlayer(song.getPath());

        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                musicPlayer.setVolume(volume,volume);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarTime.setMax(musicPlayer.getDuration());
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    musicPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Thread which manages sound bar corresponding to the progress of the music
        new Thread(() -> {
            while(musicPlayer != null){
                if(musicPlayer.isPlaying()){
                    try{
                        final double current = musicPlayer.getCurrentPosition();
                        final String elapsedTime = millisecondsToString((int) current);

                        runOnUiThread(() -> {
                            tvTime.setText(elapsedTime);
                            seekBarTime.setProgress((int)current);
                        });
                        Thread.sleep(1000);
                    }catch(InterruptedException ignored){}
                }
            }
        }).start();

        ivPlay.setOnClickListener(this);

        ivPlaylist.setOnClickListener(v -> {
            Intent openPlaylist = new Intent(MusicPlayerActivity.this,PlaylistActivity.class);
            openPlaylist.putExtra("song", song);
            openPlaylist.putExtra("activity", "musicPlayer");
            startActivity(openPlaylist);
        });

        ivPrevious.setOnClickListener(v -> {
            //We start to the previous music if the user click on the previous button
            changeSong("previous");
        });

        ivNext.setOnClickListener(v -> {
            //We start to the next music if the user click on the next button
            changeSong("next");
        });
    }

    //If the cell phone detects a change in the light in the room, the next music starts
    final SensorEventListener mSensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
        public void onSensorChanged(SensorEvent sensorEvent) {
           int newPositionInteger = musicPlayer.getCurrentPosition();

            if(newPositionInteger >= durationInteger-3){
                changeSong("next");
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener( mSensorEventListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(mSensorEventListener,sensor);
    }


    public String millisecondsToString(int time){
        String elapsedTime="";
        int minutes = time/1000 /60;
        int seconds = time /1000 % 60;
        elapsedTime = minutes + ":";
        if(seconds <10){
            elapsedTime += "0";
        }
        elapsedTime += seconds;

        return elapsedTime;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.ivPlay){
            if(musicPlayer.isPlaying()){
                //is playing
                musicPlayer.pause();
                ivPlay.setBackgroundResource(R.drawable.ic_play);
            }
            else{
                // on pause
                musicPlayer.start();
                ivPlay.setBackgroundResource(R.drawable.ic_pause);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            if(musicPlayer.isPlaying()){
                musicPlayer.stop();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(musicPlayer.isPlaying()){
            musicPlayer.stop();
        }
        Intent openListMusic = new Intent(MusicPlayerActivity.this,ListMusicActivity.class);
        startActivity(openListMusic);
    }

    //media player initialization with the song
    public void preparedMediaPlayer(String pathSong){
        try {
            musicPlayer.setDataSource(pathSong);
            musicPlayer.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }

        musicPlayer.seekTo(0);
        duration = millisecondsToString(musicPlayer.getDuration());
        durationInteger=musicPlayer.getDuration();
        tvDuration.setText(duration);
    }

    //Change of music (previous or next depending on the button clicked by the user)
    public void changeSong(String nextOrPrevious){
        if(nextOrPrevious.equals("previous")){
            if(position==0){
                position=(songArrayList.size()-1);
            }
            else{
                position--;
            }
        }
        else{
            if(position==(songArrayList.size()-1)){
                position=0;
            }
            else{
                position++;
            }
        }
        Song songPreviousOrNext = songArrayList.get(position);
        tvTitle.setText(songPreviousOrNext.getTitle());
        tvArtist.setText(songPreviousOrNext.getArtist());

        musicPlayer.reset();
        preparedMediaPlayer(songPreviousOrNext.getPath());
        ivPlay.setBackgroundResource(R.drawable.ic_pause);
        seekBarTime.setMax(musicPlayer.getDuration());
        musicPlayer.start();
        song = songPreviousOrNext;
    }
}

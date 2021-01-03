package com.devapp.musicapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;


public class ListMusicActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION=99;
    public SongAdapter songAdapter;
    ArrayList<Song> songArrayList;
    ListView lvSongs;
    ImageView ivMyPlaylist;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);

        lvSongs = findViewById(R.id.lvSongs);
        ivMyPlaylist = findViewById(R.id.ivMyplaylist);

        songArrayList = new ArrayList<>();
        songAdapter = new SongAdapter(this,songArrayList);
        lvSongs.setAdapter(songAdapter);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION);
            return;
        }
        else{
            //We recover all the music from the internal storage of the phone
            getSongs();
        }

        lvSongs.setOnItemClickListener((parent, view, position, id) -> {
            Song song = songArrayList.get(position);
            // pass the serializable Arraylist in Intent using Bundle
            Bundle extra = new Bundle();
            extra.putSerializable("songs", songArrayList);

            Intent openMusicPlayer = new Intent(getBaseContext(), MusicPlayerActivity.class);
            openMusicPlayer.putExtra("song", song);
            openMusicPlayer.putExtra("position", position);
            openMusicPlayer.putExtra("extra", extra);
            startActivity(openMusicPlayer);
        });

        ivMyPlaylist.setOnClickListener(v -> {
            Intent openPlaylist = new Intent(ListMusicActivity.this,PlaylistActivity.class);
            openPlaylist.putExtra("activity","listMusic");
            startActivity(openPlaylist);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getSongs();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getSongs(){
        // read songs from phone
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null,null,null);

        if(songCursor != null && songCursor.moveToFirst()){
            int indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                String title=songCursor.getString(indexTitle);
                String artist=songCursor.getString(indexArtist);
                String path=songCursor.getString(indexData);
                songArrayList.add(new Song(title,artist,path));
            } while (songCursor.moveToNext());
        }
        songAdapter.notifyDataSetChanged();
    }
}
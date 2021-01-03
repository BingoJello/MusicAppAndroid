package com.devapp.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    ImageView ivAddNewPlaylist,ivCloseBlockPlaylist,ivBackArrow;
    TextView tvValidationPlaylist;
    Button btnAddPlaylist,btnValidationPlaylist;
    EditText etPlaylist;
    RelativeLayout rlBlockAddPlaylist,rlBlockValidationPlaylist;
    ListView lvPlaylist,lvSongs;
    ArrayList<Song> songArrayList;
    ArrayList<String> playlistArrayList;
    PlaylistAdapter playlistAdapter;
    SongAdapter songAdapter;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        tvValidationPlaylist = findViewById(R.id.tvTextValidationPlaylist);
        ivAddNewPlaylist = findViewById(R.id.ivAddNewPlaylist);
        ivCloseBlockPlaylist = findViewById(R.id.ivCloseBlockPlaylist);
        ivBackArrow = findViewById(R.id.ivBackArrow);
        rlBlockAddPlaylist = findViewById(R.id.rlBlockAddPlaylist);
        rlBlockValidationPlaylist = findViewById(R.id.rlBlockValidationPlaylist);
        btnAddPlaylist = findViewById(R.id.btnAddPlaylist);
        btnValidationPlaylist = findViewById((R.id.btnValidationPlaylist));
        etPlaylist = findViewById(R.id.etNamePlaylist);
        lvPlaylist = findViewById(R.id.lvPlaylist);
        lvSongs = findViewById(R.id.lvSongs);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHandler(this);

        songArrayList = new ArrayList<>();
        songAdapter = new SongAdapter(this,songArrayList);
        lvSongs.setAdapter(songAdapter);

        playlistArrayList = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(this,playlistArrayList);
        lvPlaylist.setAdapter(playlistAdapter);

        ivAddNewPlaylist.setOnClickListener(v -> rlBlockAddPlaylist.setVisibility(View.VISIBLE));

        ivCloseBlockPlaylist.setOnClickListener(v -> rlBlockAddPlaylist.setVisibility(View.INVISIBLE));

        btnValidationPlaylist.setOnClickListener(v -> rlBlockValidationPlaylist.setVisibility(View.INVISIBLE));

        btnAddPlaylist.setOnClickListener(v -> {
            if(!etPlaylist.getText().toString().equals("")) {
                // We save the playlist in the database
                db.addRowPlaylist(etPlaylist.getText().toString());
                rlBlockAddPlaylist.setVisibility(View.INVISIBLE);
                playlistArrayList.clear();
                printListPlaylist();
            }
        });

        lvSongs.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            Song song = songArrayList.get(arg2);
            Bundle extra = new Bundle();
            extra.putSerializable("songs", songArrayList);
            Intent openMusicPlayer = new Intent(getBaseContext(), MusicPlayerActivity.class);
            openMusicPlayer.putExtra("song", song);
            openMusicPlayer.putExtra("position", arg2);
            openMusicPlayer.putExtra("extra", extra);
            startActivity(openMusicPlayer);
        });

        if(getIntent().getSerializableExtra("activity").equals("musicPlayer")){
            lvPlaylist.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                Song song = (Song) getIntent().getSerializableExtra("song");
                db.addRowMusic(song, arg2);
                rlBlockValidationPlaylist.setVisibility(View.VISIBLE);
            });
        }
        else{
            lvPlaylist.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                lvPlaylist.setVisibility(View.INVISIBLE);
                lvSongs.setVisibility(View.VISIBLE);
                ivAddNewPlaylist.setVisibility(View.INVISIBLE);
                ivBackArrow.setVisibility(View.VISIBLE);

                ivBackArrow.setOnClickListener(v -> {
                    lvPlaylist.setVisibility(View.VISIBLE);
                    lvSongs.setVisibility(View.INVISIBLE);
                    ivBackArrow.setVisibility(View.INVISIBLE);
                    ivAddNewPlaylist.setVisibility(View.VISIBLE);
                });
                List<Song> musicPlaylist = db.getAllRowsMusic(arg2);
                printListMusic(musicPlaylist);
            });
        }
        printListPlaylist();
    }

    public void printListPlaylist(){
        final List<String> playlist = db.getAllRowsPlaylist();

        for (String s : playlist) {
            playlistArrayList.add(s);
        }
        playlistAdapter.notifyDataSetChanged();
    }


    private void printListMusic( List<Song> listSongs){
        for (Song s : listSongs) {
            songArrayList.add(s);
        }
        songAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
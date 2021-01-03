package com.devapp.musicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "playlistManager";

    // Table name
    private static final String TABLE_PLAYLIST= "playlist";
    private static final String TABLE_MUSIC = "music";

    // Table columns names TABLE_PLAYLIST
    private static final String KEY_ID_PLAYLIST = "id_playlist";
    private static final String KEY_NAME_PLAYLIST = "name_playlist";

    // Table columns names TABLES_MUSIC
    private static final String KEY_ID_MUSIC = "id_music";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_PATH = "path";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST+ "("
                + KEY_ID_PLAYLIST + " INTEGER PRIMARY KEY," + KEY_NAME_PLAYLIST + " TEXT)";
        db.execSQL(CREATE_PLAYLIST_TABLE);

        String CREATE_MUSIC_TABLE = "CREATE TABLE " + TABLE_MUSIC+ "("
                + KEY_ID_MUSIC + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_ARTIST +
                " TEXT," + KEY_PATH + " TEXT," + KEY_ID_PLAYLIST + " INTEGER)";
        db.execSQL(CREATE_MUSIC_TABLE);
    }

    // Upgrade the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC);

        onCreate(db);
    }

    // Add a new row to the playlist table
    void addRowPlaylist(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME_PLAYLIST, name);

        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }

    // Add a new row to the music table
    void addRowMusic(Song song,int idPlaylist) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, song.getTitle());
        values.put(KEY_ARTIST, song.getArtist());
        values.put(KEY_PATH, song.getPath());
        values.put(KEY_ID_PLAYLIST, idPlaylist);

        db.insert(TABLE_MUSIC, null, values);
        db.close();
    }

    // Get all rows of the playlist table
    public List<String> getAllRowsPlaylist() {
        List<String> l = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PLAYLIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                l.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return l;
    }

    // Get all rows of the music table
    public List<Song> getAllRowsMusic(int idPlaylist) {
        List<Song> l = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_MUSIC+ " WHERE " +KEY_ID_PLAYLIST +
                " = "+ idPlaylist;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setTitle(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setPath(cursor.getString(3));
                l.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return l;
    }

    // Clear the table
    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLAYLIST);

        db.execSQL("DELETE FROM " + TABLE_MUSIC);
        db.close();
    }
}
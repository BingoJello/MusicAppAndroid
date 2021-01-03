package com.devapp.musicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<String> {
    public PlaylistAdapter(@NonNull Context context, @NonNull List<String> objects){
        super(context, 0,objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist,null);
        TextView tvPlaylist = convertView.findViewById(R.id.tvPlaylist);

        String playlist= getItem(position);
        tvPlaylist.setText(playlist);

        return convertView;
    }
}
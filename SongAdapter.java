package com.example.deadmadness.minnieplayer;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * Created by deadmadness on 11/12/15.
 */
public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInflater;

    public SongAdapter(Context c, ArrayList<Song> songList) {
        songs = songList;
        songInflater = LayoutInflater.from(c);
    }

    //return size of song list
    @Override
    public int getCount() {
        return songs.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // get the view needed
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //map to the song layout
        LinearLayout songLayout = (LinearLayout)songInflater.inflate(R.layout.song, parent, false);

        //get the title and artist view
        TextView titleView = (TextView)songLayout.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLayout.findViewById(R.id.song_artist);

        //get the song using position
        Song currentSong = songs.get(position);

        //get the title and artist strings
        titleView.setText(currentSong.getTitle());
        artistView.setText(currentSong.getArtist());

        //set position as a tag
        songLayout.setTag(position);

        return songLayout;
    }
}

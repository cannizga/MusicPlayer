package com.example.musicplayer.Albums;

import static com.example.musicplayer.MainActivity.isAlbum;
import static com.example.musicplayer.MainActivity.isPlayer;
import static com.example.musicplayer.Player.changed;
import static com.example.musicplayer.Player.isInPlayer;
import static com.example.musicplayer.Player.tempPosition;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplay;
import com.example.musicplayer.AudioModel;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.Songs.SongsFrag;

import java.util.ArrayList;

public class AlbumFrag extends Fragment implements AlbumAdapter.OnAlbumListener {
    private RecyclerView mRecyclerView;
    AlbumAdapter albumAdapter;
    public static ArrayList <AudioModel> audioModel;
    public static Context context;
    final GridLayoutManager inner = new GridLayoutManager(getContext(), 2);

    public AlbumFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        albumAdapter = new AlbumAdapter(getContext(), SongsFrag.albumDisp, this);
    }

    public static Context getcontext(){
        return context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inner.setItemPrefetchEnabled(true);
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rView1);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(250);
        albumAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(albumAdapter);
        mRecyclerView.setLayoutManager(inner);
        return view;
    }

    public ArrayList <AudioModel> getAllAudioFromDevice(final Context context) {
        final ArrayList <AudioModel> tempAudioList = new ArrayList<>();
        ArrayList <String> duplicate = new ArrayList<>();
        //albums.clear();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.DURATION, MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER, MediaStore.Audio.AudioColumns.NUM_TRACKS};
        Cursor c = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                String name = c.getString(1);
                String album = c.getString(2);
                String artist = c.getString(3);
                int ID = c.getInt(4);
                long duration = c.getLong(5);
                int year = c.getInt(6);
                String nbr = c.getString(7);
                String numTracks = c.getString(8);

                AudioModel audioModel = new AudioModel(path, name, album, artist, ID, duration, year, nbr, numTracks);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                //tempAudioList.add(audioModel);

                if (!duplicate.contains(album))
                {
                    tempAudioList.add(audioModel);
                    duplicate.add(album);
                }

            }
            c.close();
        }

        return tempAudioList;
    }

    @Override
    public void onAlbumClick(int position) {
        final String TAG = "Album";
        Log.d(TAG, "Clicked: " + position);
        //changed = true;
        Intent intent = new Intent(this.getContext(), AlbumDisplay.class);
        intent.putExtra("albumName", SongsFrag.albumDisp.get(position).getaAlbum());
        intent.putExtra("artist", SongsFrag.albumDisp.get(position).getaArtist());
        intent.putExtra("pos", position);
        isInPlayer = false;
        isPlayer = false;
        this.getContext().startActivity(intent);
    }



}
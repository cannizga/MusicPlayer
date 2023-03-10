package com.example.musicplayer.Favorites;

import static com.example.musicplayer.Songs.SongsFrag.audioModel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.AudioModel;
import com.example.musicplayer.R;
import com.example.musicplayer.Songs.SongsFrag;

import java.util.ArrayList;

import timber.log.Timber;

public class FavoriteFrag extends Fragment {
    ArrayList <AudioModel> favorites = new ArrayList<>();

    public FavoriteFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(audioModel != null) {
            favorites = audioModel;

            int j = 0;
            for (int i = 0; i < audioModel.size(); i++) {
                if (favorites.equals(audioModel.get(i).getFavorite())) {
                    {
                        favorites.add(j, SongsFrag.audioModel.get(i));
                        Timber.d("onCreateView: " + favorites.get(i).getaName());
                        //AudioModel currentAlbum = audioModel.get(i);
                    }
                    j++;
                }

            }
        }
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SongsFrag.audioModel != null) {
            favorites = SongsFrag.audioModel;

            int j = 0;
            for (int i = 0; i < SongsFrag.audioModel.size(); i++) {
                if (favorites.equals(SongsFrag.audioModel.get(i).getFavorite())) {
                    {
                        favorites.add(j, SongsFrag.audioModel.get(i));
                        Timber.d("onCreateView: " + favorites.get(i).getaName());
                        //AudioModel currentAlbum = audioModel.get(i);
                    }
                    j++;
                }

            }
        }
    }
}
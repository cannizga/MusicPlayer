package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicplayer.Albums.AlbumFrag;
import com.example.musicplayer.Favorites.FavoriteFrag;
import com.example.musicplayer.Songs.SongsFrag;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 1:
                return new AlbumFrag();
            case 2:
                return new FavoriteFrag();
        }

        return new SongsFrag();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

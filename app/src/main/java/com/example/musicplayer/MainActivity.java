package com.example.musicplayer;

import static com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplay.getPos;
import static com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplay.pass;
import static com.example.musicplayer.Player.changed;
import static com.example.musicplayer.Player.tempPosition;
import static com.example.musicplayer.Songs.SongAdapter.metaData;
import static com.example.musicplayer.Songs.SongAdapter.update;
import static com.example.musicplayer.Songs.SongsFrag.audioModel;
import static com.example.musicplayer.Songs.SongsFrag.pos;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayer.Songs.SongsFrag;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener{
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;
    public static TextView miniName, miniArtist, miniDur;
    public static ImageView miniImage, miniNext, miniPlay, miniPrev, miniClock;
    public static SeekBar miniSeek;
    public static final int REQUEST_CODE = 1;
    public static RelativeLayout mContainer;
    int prev;
    Button miniClick;
    public static Context context;
    double current_pos, total_duration;
    public static boolean isAlbum = false, pressed = false, isUpdate = false;
    public static boolean clicked = false;
    public static boolean complete = false;
    public static boolean isPlayer = false;
    public static ArrayList <AudioModel> keepList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        initView();
        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Songs"));
        tabLayout.addTab(tabLayout.newTab().setText("Albums"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorites"));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        permission();
        expandPlayer();
    }

    public static Context getPlayerContext(){
        return context;
    }

    public void initView() {
        miniClick = findViewById(R.id.click);
        miniClock = findViewById(R.id.mini_clock);
        miniSeek = findViewById(R.id.mini_seek);
        miniNext = findViewById(R.id.mini_next);
        miniPlay = findViewById(R.id.mini_playstop);
        miniPrev = findViewById(R.id.mini_prev);
        mContainer = findViewById(R.id.miniBack);
        miniDur = findViewById(R.id.mini_duration);
        miniImage = findViewById(R.id.mini_img);
        miniArtist = findViewById(R.id.mini_artist);
        miniName = findViewById(R.id.mini_name);
        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager);
    }

    public void expandPlayer() {
        miniClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressed = true;
                if(!isAlbum && !isUpdate) {
                    if(changed) {
                        pos = tempPosition;
                        changed = false;
                    }
                    Intent intent = new Intent(MainActivity.this, Player.class);
                    intent.putExtra("song", audioModel.get(pos).getaName());
                    intent.putExtra("artist", audioModel.get(pos).getaArtist());
                    intent.putExtra("path", audioModel.get(pos).getaPath());
                    intent.putExtra("dur", audioModel.get(pos).getaDuration());
                    intent.putExtra("nbr", audioModel.get(pos).getTotalTracks());
                    intent.putExtra("sender", "SongsFrag");
                    intent.putExtra("position", pos);
                    tempPosition = pos;
                    startActivity(intent);
                }
                else if(isUpdate && update) {
                    if(changed) {
                        pos = tempPosition;
                        changed = false;
                    }
                    Intent intent = new Intent(MainActivity.this, Player.class);
                    intent.putExtra("song", metaData.get(pos).getaName());
                    intent.putExtra("artist", metaData.get(pos).getaArtist());
                    intent.putExtra("path", metaData.get(pos).getaPath());
                    intent.putExtra("dur", metaData.get(pos).getaDuration());
                    intent.putExtra("nbr", metaData.get(pos).getTotalTracks());
                    intent.putExtra("sender", "searchSong");
                    intent.putExtra("position", pos);
                    tempPosition = pos;
                    startActivity(intent);
                }
                else
                {
                    if(changed) {
                        getPos = tempPosition;
                        changed = false;
                        clicked = true;
                    }
                        Intent intent = new Intent(MainActivity.this, Player.class);
                        intent.getStringExtra("pass");
                        intent.putExtra("song", pass.get(getPos).getaName());
                        intent.putExtra("artist", pass.get(getPos).getaArtist());
                        intent.putExtra("path", pass.get(getPos).getaPath());
                        intent.putExtra("dur", pass.get(getPos).getaDuration());
                        intent.putExtra("nbr", pass.get(getPos).getTotalTracks());
                        intent.putExtra("sender", "albumSong");
                        intent.putExtra("position", getPos);
                        startActivity(intent);
                        clicked = false;
                        tempPosition = getPos;
                        prev = getPos;
                }
            }
        });
    }

    private void permission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
        else
        {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList <AudioModel> search = new ArrayList<>();
        String userIn = newText.toLowerCase();
        for(AudioModel song : audioModel){
            if(song.getaName().toLowerCase().contains(userIn) || song.getaArtist().toLowerCase().contains(userIn)){
                search.add(song);
            }
        }
        SongsFrag.songAdapter.updateList(search);
        return true;
    }
}
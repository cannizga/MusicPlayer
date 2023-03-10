package com.example.musicplayer;

import static com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplay.getPos;
import static com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplay.pass;
import static com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplay.playerCheck;
import static com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplayAdapter.albumMetaData;
import static com.example.musicplayer.MainActivity.complete;
import static com.example.musicplayer.MainActivity.getPlayerContext;
import static com.example.musicplayer.MainActivity.isPlayer;
import static com.example.musicplayer.MainActivity.mContainer;
import static com.example.musicplayer.MainActivity.miniArtist;
import static com.example.musicplayer.MainActivity.miniClock;
import static com.example.musicplayer.MainActivity.miniDur;
import static com.example.musicplayer.MainActivity.miniImage;
import static com.example.musicplayer.MainActivity.miniName;
import static com.example.musicplayer.MainActivity.miniNext;
import static com.example.musicplayer.MainActivity.miniPlay;
import static com.example.musicplayer.MainActivity.miniPrev;
import static com.example.musicplayer.MainActivity.miniSeek;
import static com.example.musicplayer.Songs.SongAdapter.metaData;
import static com.example.musicplayer.Songs.SongsFrag.audioModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplay;
import com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplayAdapter;
import com.example.musicplayer.Songs.SongsFrag;

import java.util.ArrayList;
import java.util.Random;

public class Player extends AppCompatActivity {
    String songName = "name";
    String artistName = "art";
    String path = "path";
    long dur = 0;
    public static int pos = -1;
    public static ImageView albumPhoto, next, prev, playStop, restart, back, forward, fav, add, shuffle;
    public static TextView tvAlbName, artName, totalDuration, startDuration, trackNbr;
    SeekBar seek;
    double current_pos, total_duration;
    ArrayList<AudioModel> Song;
    public static  MediaPlayer mediaPlayer = new MediaPlayer();
    public static boolean changed = false;
    public static int tempPosition;
    public static int send, savPrev;
    public static boolean isInPlayer = false;
    public static boolean favorite = false, shuffleBoolean = false;
    public static String sender;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        initViews();
        intentSetup();
        seek.setProgress((int) current_pos);
        if(mediaPlayer.isPlaying()) {
            playStop.setImageResource(R.drawable.ic_pause);
        } else {
            playStop.setImageResource(R.drawable.ic_play);
        }

        if(audioModel.get(pos).getFavorite().equals("true")) {
            fav.setImageResource(R.drawable.ic_favorite_fill);
        }
        else {
            fav.setImageResource(R.drawable.ic_favorite);
        }

        if(shuffleBoolean)
        {
            shuffle.setImageResource(R.drawable.ic_shuffle_on);
        }
        else {
            shuffle.setImageResource(R.drawable.ic_shuffle_off);
        }

        if(changed) {
            tvAlbName.setText(Song.get(tempPosition).getaName());
            artName.setText(Song.get(tempPosition).getaArtist());
            totalDuration.setText(timerConversion(Song.get(tempPosition).getaDuration()));
            path = Song.get(tempPosition).getaPath();
            changed = false;
        }
        else {
            tvAlbName.setText(Song.get(pos).getaName());
            artName.setText(Song.get(pos).getaArtist());
            totalDuration.setText(timerConversion(Song.get(pos).getaDuration()));
        }

        getImage();
        getMetaData(path);
        setListeners();
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void intentSetup () {
        songName = getIntent().getStringExtra("song");
        artistName = getIntent().getStringExtra("artist");
        path = getIntent().getStringExtra("path");
        dur = getIntent().getLongExtra("dur", 0);
        pos = getIntent().getIntExtra("position", -1);
        sender = getIntent().getStringExtra("sender");
        if(sender != null && sender.equals("SongsFrag")) {Song = audioModel;}
        else if (sender != null && sender.equals("searchSong")) {Song = metaData;}
        else if (sender != null && sender.equals("albumSong")) {Song = pass;}
    }

    public void setListeners() {
        seek();
        setAudioProgress();
        prevAudio();
        onFav();
        nextAudio();
        playStop();
        onCompleted();
        restart();
        back();
        forward();
        onShuffle();
    }

    public void initViews() {
        tvAlbName = findViewById(R.id.tvSong);
        albumPhoto = findViewById(R.id.albumImage);
        artName = findViewById(R.id.tvArtistName);
        totalDuration = findViewById(R.id.durationTotal);
        startDuration = findViewById(R.id.durationPlayed);
        seek = findViewById(R.id.seekBar);
        next = findViewById(R.id.next);
        playStop = findViewById(R.id.play);
        prev = findViewById(R.id.prev);
        trackNbr = findViewById(R.id.trackNbr);
        restart = findViewById(R.id.ivStartBeginning);
        back = findViewById(R.id.ivReplayBack);
        forward = findViewById(R.id.ivReplayForward);
        fav = findViewById(R.id.ivFav);
        add = findViewById(R.id.ivAdd);
        shuffle = findViewById(R.id.ivShuffle);
    }

    public void playAudio(int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.stop();
            Uri uri = Uri.parse(Song.get(position).getaPath());
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAudioProgress();
    }

    public void onCompleted() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                changed = true;
                if (pos < (Song.size() - 1) && !mediaPlayer.isPlaying()) {
                    if(shuffleBoolean){
                        savPrev = pos;
                        pos = getRandom(Song.size());
                        AlbumDisplay.pos = pos;
                    } else { pos++; AlbumDisplay.pos++; }
                    tvAlbName.setText(Song.get(pos).getaName());
                    artName.setText(Song.get(pos).getaArtist());
                    dur = Song.get(pos).getaDuration();
                    totalDuration.setText(timerConversion(dur));
                    getMetaData(Song.get(pos).getaPath());
                    tempPosition = pos;
                    if (mediaPlayer.isPlaying()) {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    }
                    playAudio(pos);
                } else {
                    if(!mediaPlayer.isPlaying()) {
                        pos = 0;
                        AlbumDisplay.pos = 0;
                        tvAlbName.setText(Song.get(pos).getaName());
                        artName.setText(Song.get(pos).getaArtist());
                        dur = Song.get(pos).getaDuration();
                        totalDuration.setText(timerConversion(dur));
                        getMetaData(Song.get(pos).getaPath());
                        tempPosition = pos;
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        }
                        playAudio(pos);
                    }
                }
            }
        });
    }

    public void prevAudio() {
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumDisplay.playerCheck = false;
                changed = true;
                if (pos > 0) {
                    if(shuffleBoolean){
                        pos = savPrev;
                        AlbumDisplay.pos = savPrev;
                    } else { pos--; AlbumDisplay.pos--;}
                    tvAlbName.setText(Song.get(pos).getaName());
                    artName.setText(Song.get(pos).getaArtist());
                    dur = Song.get(pos).getaDuration();
                    totalDuration.setText(timerConversion(dur));
                    onCompleted();
                    getMetaData(Song.get(pos).getaPath());
                    tempPosition = pos;
                    if (mediaPlayer.isPlaying()) {
                        playStop.setImageResource(R.drawable.ic_pause);
                    } else {
                        playStop.setImageResource(R.drawable.ic_pause);//this should be ic_play
                    }
                    playAudio(pos);
                } else {
                    pos = Song.size() - 1;
                    AlbumDisplay.pos = Song.size() - 1;
                    tvAlbName.setText(Song.get(pos).getaName());
                    artName.setText(Song.get(pos).getaArtist());
                    dur = Song.get(pos).getaDuration();
                    totalDuration.setText(timerConversion(dur));
                    onCompleted();
                    getMetaData(Song.get(pos).getaPath());
                    tempPosition = pos;
                    if (mediaPlayer.isPlaying()) {
                        playStop.setImageResource(R.drawable.ic_pause);
                    } else {
                        playStop.setImageResource(R.drawable.ic_pause);
                    }
                    playAudio(pos);
                }
            }
        });
    }

    public void nextAudio() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changed = true;
                AlbumDisplay.playerCheck = false;
                if (pos < (Song.size() - 1)) {
                    if(shuffleBoolean){
                        savPrev = pos;
                        pos = getRandom(Song.size() - 1);
                        AlbumDisplay.pos = pos;
                    } else { pos++; AlbumDisplay.pos++; }

                    tvAlbName.setText(Song.get(pos).getaName());
                    artName.setText(Song.get(pos).getaArtist());
                    dur = Song.get(pos).getaDuration();
                    totalDuration.setText(timerConversion(dur));
                    onCompleted();
                    getMetaData(Song.get(pos).getaPath());
                    tempPosition = pos;
                    if (mediaPlayer.isPlaying()) {
                        playStop.setImageResource(R.drawable.ic_pause);
                    }
                    playAudio(pos);
                } else {
                    pos = 0;
                    AlbumDisplay.pos = 0;
                    tvAlbName.setText(Song.get(pos).getaName());
                    artName.setText(Song.get(pos).getaArtist());
                    dur = Song.get(pos).getaDuration();
                    totalDuration.setText(timerConversion(dur));
                    onCompleted();
                    getMetaData(Song.get(pos).getaPath());
                    tempPosition = pos;
                    if (mediaPlayer.isPlaying()) {
                        playStop.setImageResource(R.drawable.ic_pause);
                    }
                    playAudio(pos);
                }
            }
        });
    }

    public void playStop() {
        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playStop.setImageResource(R.drawable.ic_play);
                } else {
                    mediaPlayer.start();
                    playStop.setImageResource(R.drawable.ic_pause);
                }
            }
        });
    }

    public void restart() {
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(0);
            }
        });
    }

    public void back() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 30000);
            }
        });
    }

    public void forward() {
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 30000);
            }
        });
    }

    public void seek() {
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_pos = seekBar.getProgress();
                mediaPlayer.seekTo((int) current_pos);
            }
        });
    }

    public void onFav() {
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favorite)
                {
                    favorite = false;
                    Song.get(pos).setFavorite("false");
                    audioModel.get(pos).setFavorite("false");
                    fav.setImageResource(R.drawable.ic_favorite);
                }
                else
                {
                    favorite = true;
                    Song.get(pos).setFavorite("true");
                    audioModel.get(pos).setFavorite("true");
                    fav.setImageResource(R.drawable.ic_favorite_fill);
                }
            }
        });
    }

    public void onShuffle() {
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleBoolean)
                {
                    shuffleBoolean = false;
                    shuffle.setImageResource(R.drawable.ic_shuffle_off);
                }
                else
                {
                    shuffleBoolean = true;
                    shuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
    }

    public void getImage() {
        //byte[] image = getAlbumArt(Song.get(pos).getaPath());
        byte [] image = getAlbumArt(path);
        if(image != null)
        {
            Glide.with(this).load(image).apply(RequestOptions.circleCropTransform()).into(albumPhoto);
        }
        else
        {
            Glide.with(this).load(R.drawable.ic_album).into(albumPhoto);
        }

    }
    //set audio progress
    public void setAudioProgress() {
        //get the audio duration
        current_pos = mediaPlayer.getCurrentPosition();
        total_duration = mediaPlayer.getDuration();

        //display the audio duration
        startDuration.setText(timerConversion((long) current_pos));
        seek.setMax((int) total_duration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    current_pos = mediaPlayer.getCurrentPosition();
                    startDuration.setText(timerConversion((long) current_pos));
                    seek.setProgress((int) current_pos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed) {
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }
    //time conversion
    public String timerConversion(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            audioTime = String.format("%2d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%2d:%02d", mns, scs);
        }
        return audioTime;
    }

    public void getMetaData(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null)
        {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, albumPhoto, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if ( swatch != null)
                    {
                        ConstraintLayout mContainer = findViewById(R.id.Container);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        tvAlbName.setTextColor(swatch.getBodyTextColor());
                        artName.setTextColor(swatch.getBodyTextColor());
                        prev.setColorFilter(swatch.getBodyTextColor());
                        playStop.setColorFilter(swatch.getBodyTextColor());
                        next.setColorFilter(swatch.getBodyTextColor());
                        seek.getThumb().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_ATOP);
                        totalDuration.setTextColor(swatch.getBodyTextColor());
                        startDuration.setTextColor(swatch.getBodyTextColor());
                    }
                    else
                    {
                        ConstraintLayout mContainer = findViewById(R.id.Container);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        tvAlbName.setTextColor(Color.WHITE);
                        artName.setTextColor(Color.WHITE);
                        seek.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                    }
                }
            });

        }
        else
        {
            Glide.with(this).asBitmap().apply(RequestOptions.circleCropTransform()).load(R.drawable.ic_album).into(albumPhoto);
            ConstraintLayout mContainer = findViewById(R.id.Container);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            tvAlbName.setTextColor(Color.WHITE);
            artName.setTextColor(Color.WHITE);
            seek.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).apply(RequestOptions.circleCropTransform()).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    public void metaData(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null)
        {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(getPlayerContext(), miniImage, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if ( swatch != null)
                    {
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        miniName.setTextColor(swatch.getBodyTextColor());
                        miniArtist.setTextColor(swatch.getBodyTextColor());
                        miniNext.setColorFilter(swatch.getBodyTextColor());
                        miniPlay.setColorFilter(swatch.getBodyTextColor());
                        miniPrev.setColorFilter(swatch.getBodyTextColor());
                        miniDur.setTextColor(swatch.getBodyTextColor());
                        miniClock.setColorFilter(swatch.getBodyTextColor());
                        miniSeek.getThumb().setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_ATOP);
                        miniSeek.setProgressTintMode(PorterDuff.Mode.LIGHTEN);
                    }
                    else
                    {
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        miniName.setTextColor(Color.WHITE);
                        miniArtist.setTextColor(Color.WHITE);
                        miniDur.setTextColor(Color.WHITE);
                        miniClock.setColorFilter(Color.WHITE);
                        miniSeek.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                    }
                }
            });

        }
        else
        {
            Glide.with(this).asBitmap().apply(RequestOptions.circleCropTransform()).load(R.drawable.ic_album).into(miniImage);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            miniName.setTextColor(Color.WHITE);
            miniArtist.setTextColor(Color.WHITE);
            miniDur.setTextColor(Color.WHITE);
            miniClock.setColorFilter(Color.WHITE);
            miniSeek.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(playerCheck) {

        }else {
            miniName.setText(Song.get(pos).getaName());
            miniArtist.setText(Song.get(pos).getaArtist());
            AudioModel currentAlbum = Song.get(pos);
            metaData(Song.get(pos).getaPath());
        }
        send = tempPosition;
        SongsFrag.pos = tempPosition;
        isPlayer = false;
        if(mediaPlayer.isPlaying()){
            miniPlay.setImageResource(R.drawable.ic_pause);
        } else {
            miniPlay.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(this, AlbumDisplay.class);
                intent.putExtra("albumName", Song.get(pos).getaAlbum());
                intent.putExtra("artist", Song.get(pos).getaArtist());
                intent.putExtra("pos", pos);
                isInPlayer = true;
                isPlayer = true;
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(playerCheck) {
            Song = pass;
            pos = getPos;
        }
    }
}

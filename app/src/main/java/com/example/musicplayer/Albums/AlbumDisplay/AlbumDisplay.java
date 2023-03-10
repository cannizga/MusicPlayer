package com.example.musicplayer.Albums.AlbumDisplay;

import static com.example.musicplayer.MainActivity.complete;
import static com.example.musicplayer.MainActivity.isAlbum;
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
import static com.example.musicplayer.Player.changed;
import static com.example.musicplayer.Player.fav;
import static com.example.musicplayer.Player.isInPlayer;
import static com.example.musicplayer.Player.mediaPlayer;
import static com.example.musicplayer.Player.savPrev;
import static com.example.musicplayer.Player.shuffleBoolean;
import static com.example.musicplayer.Player.tempPosition;
import static com.example.musicplayer.Songs.SongsFrag.audioModel;

import android.content.Context;
import android.database.Cursor;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.Albums.AlbumFrag;
import com.example.musicplayer.AudioModel;
import com.example.musicplayer.Player;
import com.example.musicplayer.R;
import com.example.musicplayer.Songs.SongsFrag;

import java.util.ArrayList;
import java.util.Random;

public class AlbumDisplay extends AppCompatActivity implements AlbumDisplayAdapter.OnAlbumListener {
    RecyclerView recyclerView;
    ImageView albumPhoto;
    TextView tvAlbName, artName, Duration;
    RelativeLayout toolbar;
    RelativeLayout bg;
    double current_pos, total_duration;
    String albumName = "name", art = "artist";
    int count = 0;
    ArrayList <AudioModel> albumSongs = new ArrayList<>();
    public static ArrayList <AudioModel> pass = new ArrayList<>();
    AlbumDisplayAdapter albumDisplayAdapter;
    long duration;
    public static int pos, pos2;
    public static int getPos;
    int tempPos;
    public static boolean onclick = false, playerCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_display);
        /*try {
            Class.forName("dalvik.system.CloseGuard")
                    .getMethod("setEnabled", boolean.class)
                    .invoke(null, true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }*/
        recyclerView = findViewById(R.id.rView3);
        albumPhoto = findViewById(R.id.albumDisplay);
        tvAlbName = findViewById(R.id.albName);
        artName = findViewById(R.id.artName);
        albumName = getIntent().getStringExtra("albumName");
        art = getIntent().getStringExtra("artist");
        Duration = findViewById(R.id.duration);
        bg = findViewById(R.id.secondBg);
        pos2 = getIntent().getIntExtra("pos", 0);
        toolbar = findViewById(R.id.bg);
        tempPos = getIntent().getIntExtra("pos", 0);

        int j = 0;
        for(int i = 0; i < SongsFrag.audioModel.size(); i++)
        {
            if(albumName.equals(SongsFrag.audioModel.get(i).getaAlbum()) && art.equals(SongsFrag.audioModel.get(i).getaArtist()))
            {
                albumSongs.add(j, SongsFrag.audioModel.get(i));
                AudioModel currentAlbum = SongsFrag.audioModel.get(i);
                tvAlbName.setText(currentAlbum.getaAlbum());
                artName.setText(currentAlbum.getaArtist());
                duration += currentAlbum.getaDuration();
                count += Integer.parseInt(currentAlbum.getTotalTracks());

                if(currentAlbum.getYear() == 0)
                {
                    Duration.setText("Unknown" + " | " + timerConversion(duration) + " | " + count);
                }
                else {
                    Duration.setText(currentAlbum.getYear() + " | " + timerConversion(duration) + " | " + count);
                }
                j++;
            }

        }

        byte[] image = getAlbumArt(albumSongs.get(0).getaPath());
        if(image != null)
        {
            Glide.with(this).load(image).apply(RequestOptions.circleCropTransform()).into(albumPhoto);
        }
        else
        {
            Glide.with(this).load(R.drawable.ic_album).into(albumPhoto);
        }

        metaDataBg(albumSongs.get(0).getaPath());

        albumDisplayAdapter = new AlbumDisplayAdapter(this, albumSongs, this);
        recyclerView.setAdapter(albumDisplayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
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

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public ArrayList <AudioModel> getAllAudioFromDevice(final Context context) {
        final ArrayList <AudioModel> tempAudioList = new ArrayList<>();
        ArrayList <String> duplicate = new ArrayList<>();
        tempAudioList.clear();

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
                    //duplicate.add(album);
                }

            }
            c.close();
        }

        return tempAudioList;
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
            if(isInPlayer) {
                ImageAnimation(SongsFrag.getcontext(), miniImage, bitmap);
            }
            else {  ImageAnimation(AlbumFrag.getcontext(), miniImage, bitmap); }
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
            Glide.with(AlbumFrag.getcontext()).asBitmap().apply(RequestOptions.circleCropTransform()).load(R.drawable.ic_album).into(miniImage);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            miniName.setTextColor(Color.WHITE);
            miniArtist.setTextColor(Color.WHITE);
            miniDur.setTextColor(Color.WHITE);
            miniClock.setColorFilter(Color.WHITE);
            miniSeek.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }

    public void metaDataBg(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null)
        {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            //ImageAnimation(AlbumFrag.getcontext(), albumPhoto, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getLightMutedSwatch();
                    Palette.Swatch swatch2 = palette.getDarkVibrantSwatch();
                    Palette.Swatch swatch3 = palette.getDominantSwatch();
                    if ( swatch != null & swatch2 != null)
                    {
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        bg.setBackground(gradientDrawableBg);

                        GradientDrawable gradientDrawableBg2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch2.getRgb(), swatch2.getRgb()});
                        toolbar.setBackground(gradientDrawableBg2);
                    }
                    else
                    {
                        bg.setBackgroundResource(R.drawable.main_bg);
                        toolbar.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{palette.getMutedColor(Color.BLACK), palette.getMutedColor(Color.BLACK)});
                        GradientDrawable gradientDrawableBg2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch3.getRgb(), swatch3.getRgb()});
                        bg.setBackground(gradientDrawableBg2);
                        toolbar.setBackground(gradientDrawableBg);
                    }
                }
            });

        }
        else
        {
            Glide.with(AlbumFrag.getcontext()).asBitmap().apply(RequestOptions.circleCropTransform()).load(R.drawable.ic_album).into(miniImage);
            bg.setBackgroundResource(R.drawable.main_bg);
            toolbar.setBackgroundResource(R.drawable.main_bg);
        }
    }

    @Override
    public void onAlbumClick(int position) {
        pass.clear();
        onclick = true;
        int j = 0;
        for(int i = 0; i < audioModel.size(); i++)
        {
            if(albumName.equals(audioModel.get(i).getaAlbum()) && art.equals(audioModel.get(i).getaArtist()))
            {
                AudioModel currentAlbum = audioModel.get(i);
                pass.add(j, audioModel.get(i));
                j++;
            }

        }
        final String TAG = "Song";
        Log.d(TAG, "Clicked: " + position + albumSongs.get(position).getaName());
        isAlbum = true;
        getPos = position;
        changed = true;
        miniName.setText(albumSongs.get(position).getaName());
        miniArtist.setText(albumSongs.get(position).getaArtist());
        metaData(albumSongs.get(position).getaPath());
        AudioModel currentAlbum = albumSongs.get(position);
        byte[] image = getAlbumArt(currentAlbum.getaPath());
        playAudio(position);
        playStop();
        nextAudio(position);
        prevAudio(position);
        onCompleted(position);
    }

    public void seek() {
        miniSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    public void setAudioProgress() {
        //get the audio duration
        current_pos = mediaPlayer.getCurrentPosition();
        total_duration = mediaPlayer.getDuration();

        //display the audio duration
        miniDur.setText(timerConversion((long) current_pos));
        miniSeek.setMax((int) total_duration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    current_pos = mediaPlayer.getCurrentPosition();
                    miniDur.setText(timerConversion((long) current_pos));
                    miniSeek.setProgress((int) current_pos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed) {
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    public void playStop() {
        miniPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    miniPlay.setImageResource(R.drawable.ic_play);
                } else {
                    mediaPlayer.start();
                    miniPlay.setImageResource(R.drawable.ic_pause);
                }
            }
        });
    }

    public void onCompleted(int posi) {
        if(changed) {
            pos = tempPosition;
            changed = false;
        } else {
            pos = posi;
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (pos < (albumSongs.size() - 1) && !mediaPlayer.isPlaying()) {
                    if(shuffleBoolean){
                        savPrev = pos;
                        pos = getRandom(albumSongs.size());
                    } else { pos++; }
                    miniName.setText(albumSongs.get(pos).getaName());
                    miniArtist.setText(albumSongs.get(pos).getaArtist());
                    metaData(albumSongs.get(pos).getaPath());
                    if (mediaPlayer.isPlaying()) {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    } else {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    }
                    playAudio(pos);
                } else {
                    if(!mediaPlayer.isPlaying()) {
                        pos = 0;
                        miniName.setText(albumSongs.get(pos).getaName());
                        miniArtist.setText(albumSongs.get(pos).getaArtist());
                        metaData(albumSongs.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        }
                        playAudio(pos);
                    }
                }
                tempPosition = pos;
                getPos = tempPosition;
            }
        });
    }

    public void playAudio(int position) {
        try {
            miniPlay.setImageResource(R.drawable.ic_pause);
            mediaPlayer.reset();
            mediaPlayer.stop();
            //set file path

            //mediaPlayer.setDataSource(this, Uri.parse(path));
            Uri uri = Uri.parse(albumSongs.get(position).getaPath());
            //mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        setAudioProgress();
    }

    public void nextAudio(int position) {
        if(changed) {
            pos = tempPosition;
            changed = false;
        } else {
            pos = position;
        }
        miniNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos < (albumSongs.size() - 1)) {
                    if(shuffleBoolean){
                        savPrev = pos;
                        pos = getRandom(albumSongs.size() - 1);
                    } else { pos++; }
                    miniName.setText(albumSongs.get(pos).getaName());
                    miniArtist.setText(albumSongs.get(pos).getaArtist());
                    metaData(albumSongs.get(pos).getaPath());
                    if (mediaPlayer.isPlaying()) {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    } else {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    }
                    playAudio(pos);
                } else {
                    pos = 0;
                    miniName.setText(albumSongs.get(pos).getaName());
                    miniArtist.setText(albumSongs.get(pos).getaArtist());
                    metaData(albumSongs.get(pos).getaPath());
                    if (mediaPlayer.isPlaying()) {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    } else {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    }
                    playAudio(pos);
                }
                tempPosition = pos;
                getPos = tempPosition;
            }
        });
    }

    public void prevAudio(int position) {
        if(changed) {
            pos = tempPosition;
            changed = false;
        } else {
            pos = position;
        }
        miniPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0) {
                    if(shuffleBoolean){
                        pos = savPrev;
                    } else { pos--; }
                    miniName.setText(albumSongs.get(pos).getaName());
                    miniArtist.setText(albumSongs.get(pos).getaArtist());
                    metaData(albumSongs.get(pos).getaPath());
                    if (mediaPlayer.isPlaying()) {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    } else {
                        miniPlay.setImageResource(R.drawable.ic_pause);//this should be ic_play
                    }
                    playAudio(pos);
                } else {
                    pos = albumSongs.size() - 1;
                    miniName.setText(albumSongs.get(pos).getaName());
                    miniArtist.setText(albumSongs.get(pos).getaArtist());
                    //dur = Song.get(pos).getaDuration();
                    //miniDur.setText(timerConversion(dur));
                    //getImage();
                    metaData(albumSongs.get(pos).getaPath());
                    if (mediaPlayer.isPlaying()) {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    } else {
                        miniPlay.setImageResource(R.drawable.ic_pause);
                    }
                    playAudio(pos);
                }
                tempPosition = pos;
                getPos = tempPosition;
            }
        });
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(changed)
        {

        }
        else {
            getPos = pos;
            changed = false;
        }

        if(!isPlayer) {

        } else if(isPlayer && onclick) {
            Player.tvAlbName.setText(pass.get(pos).getaName());
            Player.totalDuration.setText(timerConversion(pass.get(pos).getaDuration()));
            miniName.setText(pass.get(pos).getaName());
            miniArtist.setText(pass.get(pos).getaArtist());
            metaData(pass.get(pos).getaPath());
            onclick = false;
            isPlayer = false;
            playerCheck = true;
        }

        if(mediaPlayer.isPlaying()){
            miniPlay.setImageResource(R.drawable.ic_pause);
        } else {
            miniPlay.setImageResource(R.drawable.ic_play);
        }

        setAudioProgress();
        seek();
    }
}

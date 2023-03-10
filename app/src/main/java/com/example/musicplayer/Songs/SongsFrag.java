package com.example.musicplayer.Songs;

import static com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplayAdapter.albumMetaData;
import static com.example.musicplayer.MainActivity.complete;
import static com.example.musicplayer.MainActivity.isAlbum;
import static com.example.musicplayer.MainActivity.isUpdate;
import static com.example.musicplayer.MainActivity.keepList;
import static com.example.musicplayer.MainActivity.miniArtist;
import static com.example.musicplayer.MainActivity.miniClock;
import static com.example.musicplayer.MainActivity.miniDur;
import static com.example.musicplayer.MainActivity.miniImage;
import static com.example.musicplayer.MainActivity.miniName;
import static com.example.musicplayer.MainActivity.mContainer;
import static com.example.musicplayer.MainActivity.miniNext;
import static com.example.musicplayer.MainActivity.miniPlay;
import static com.example.musicplayer.MainActivity.miniPrev;
import static com.example.musicplayer.MainActivity.miniSeek;
import static com.example.musicplayer.Player.changed;
import static com.example.musicplayer.Player.mediaPlayer;
import static com.example.musicplayer.Player.savPrev;
import static com.example.musicplayer.Player.shuffleBoolean;
import static com.example.musicplayer.Player.tempPosition;
import static com.example.musicplayer.Songs.SongAdapter.metaData;
import static com.example.musicplayer.Songs.SongAdapter.update;

import android.app.NotificationManager;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.Albums.AlbumDisplay.AlbumDisplay;
import com.example.musicplayer.AudioModel;
import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.Random;

public class  SongsFrag extends Fragment implements SongAdapter.OnSongListener {
    private RecyclerView mRecyclerView;
    public static SongAdapter songAdapter;
    public static ArrayList <AudioModel> audioModel;
    public static ArrayList <AudioModel> albumDisp = new ArrayList<>();
    long dur;
    public static int pos;
    double current_pos, total_duration;
    public static int count = 0;
    public static Context SongContext;
    View view;
    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
            false);
    public SongsFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioModel = getAllAudioFromDevice(getContext());
        songAdapter = new SongAdapter(getContext(), audioModel, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_songs, container, false);
        mRecyclerView = view.findViewById(R.id.rView2);
        SongContext = this.getContext();
        mRecyclerView.setItemViewCacheSize(250);
        songAdapter.setHasStableIds(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(songAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    public static Context getcontext(){
        return SongContext;
    }

    public ArrayList <AudioModel> getAllAudioFromDevice(final Context context) {
        final ArrayList <AudioModel> tempAudioList = new ArrayList<>();
        ArrayList <String> duplicate = new ArrayList<>();
        albumDisp.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.AudioColumns.DURATION, MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER, MediaStore.Audio.AudioColumns.NUM_TRACKS, MediaStore.Audio.AudioColumns.IS_FAVORITE};
        Cursor c = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                MediaStore.Audio.AudioColumns.TITLE);

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
                String fav = c.getString(9);
                fav = "false";
                //Log.e(fav, "getAllAudioFromDevice: " + fav);

                AudioModel audioModel = new AudioModel(path, name, album, artist, ID, duration, year, nbr, numTracks, fav);
                count++;

                tempAudioList.add(audioModel);

                if (!duplicate.contains(album))
                {
                    albumDisp.add(audioModel);
                    duplicate.add(album);
                }

            }
            c.close();
        }

        return tempAudioList;
    }

    public void metaData(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null)
        {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this.getContext(), miniImage, bitmap);
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

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

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

    public void playAudio(int position) {
        try {

            miniPlay.setImageResource(R.drawable.ic_pause);
            mediaPlayer.reset();
            mediaPlayer.stop();
            //set file path
            Uri uri;
            //mediaPlayer.setDataSource(this, Uri.parse(path));
            if(update) {
                uri = Uri.parse(metaData.get(position).getaPath());
            } else {
                uri = Uri.parse(audioModel.get(position).getaPath());
            }
            //mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.setDataSource(this.getContext(), uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAudioProgress();
        seek();
    }

    public void nextAudio(int posi) {
        if(changed) {
            pos = tempPosition;
            changed = false;
        } else {
            pos = posi;
        }
        miniNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos < (audioModel.size() - 1)) {
                    if(shuffleBoolean){
                        savPrev = pos;
                        pos = getRandom(audioModel.size());
                    } else { pos++; }

                    if(update) {
                        if (pos < (keepList.size())) {
                            miniName.setText(keepList.get(pos).getaName());
                            miniArtist.setText(keepList.get(pos).getaArtist());
                            metaData(keepList.get(pos).getaPath());
                            if (mediaPlayer.isPlaying()) {
                                miniPlay.setImageResource(R.drawable.ic_pause);
                            } else {
                                miniPlay.setImageResource(R.drawable.ic_pause);
                            }
                            playAudio(pos);
                        }
                        else
                        {
                            pos = 0;
                            miniName.setText(keepList.get(pos).getaName());
                            miniArtist.setText(keepList.get(pos).getaArtist());
                            metaData(keepList.get(pos).getaPath());
                            if (mediaPlayer.isPlaying()) {
                                miniPlay.setImageResource(R.drawable.ic_pause);
                            } else {
                                miniPlay.setImageResource(R.drawable.ic_pause);
                            }
                            playAudio(pos);
                        }
                    } else {
                        miniName.setText(audioModel.get(pos).getaName());
                        miniArtist.setText(audioModel.get(pos).getaArtist());
                        metaData(audioModel.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        }
                        playAudio(pos);
                    }
                } else {
                    pos = 0;
                    if(update) {
                        miniName.setText(keepList.get(pos).getaName());
                        miniArtist.setText(keepList.get(pos).getaArtist());
                        metaData(keepList.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        }
                        playAudio(pos);
                    } else {
                        miniName.setText(audioModel.get(pos).getaName());
                        miniArtist.setText(audioModel.get(pos).getaArtist());
                        metaData(audioModel.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        }
                        playAudio(pos);
                    }
                }
                tempPosition = pos;
            }
        });
    }

    public void prevAudio(int posi) {
        if(changed) {
            pos = tempPosition;
            changed = false;
        }
        else {
            pos = posi;
        }
        miniPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0) {
                    if(shuffleBoolean){
                        pos = savPrev;
                    } else { pos--; }
                    if(update) {
                        miniName.setText(metaData.get(pos).getaName());
                        miniArtist.setText(metaData.get(pos).getaArtist());
                        metaData(metaData.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);//this should be ic_play
                        }
                        playAudio(pos);
                    } else {
                        miniName.setText(audioModel.get(pos).getaName());
                        miniArtist.setText(audioModel.get(pos).getaArtist());
                        metaData(audioModel.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);//this should be ic_play
                        }
                        playAudio(pos);
                    }
                } else {
                    pos = audioModel.size() - 1;
                    if(update) {
                        pos = metaData.size() - 1;
                        miniName.setText(metaData.get(pos).getaName());
                        miniArtist.setText(metaData.get(pos).getaArtist());
                        metaData(metaData.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);//this should be ic_play
                        }
                        playAudio(pos);
                    } else {
                        miniName.setText(audioModel.get(pos).getaName());
                        miniArtist.setText(audioModel.get(pos).getaArtist());
                        metaData(audioModel.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        }
                        playAudio(pos);
                    }
                }
                tempPosition = pos;
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
                if (pos < (audioModel.size() - 1) && !mediaPlayer.isPlaying()) {
                    complete = true;
                    if(shuffleBoolean){
                        savPrev = pos;
                        pos = getRandom(audioModel.size());
                    } else { pos++; }
                    if(update) {
                        if (pos < (metaData.size() - 1) && !mediaPlayer.isPlaying()) {
                            miniName.setText(metaData.get(pos).getaName());
                            miniArtist.setText(metaData.get(pos).getaArtist());
                            metaData(metaData.get(pos).getaPath());
                            if (mediaPlayer.isPlaying()) {
                                miniPlay.setImageResource(R.drawable.ic_pause);
                            } else {
                                miniPlay.setImageResource(R.drawable.ic_pause);
                            }
                            playAudio(pos);
                        } else {
                            pos = 0;
                            miniName.setText(metaData.get(pos).getaName());
                            miniArtist.setText(metaData.get(pos).getaArtist());
                            metaData(metaData.get(pos).getaPath());
                            if (mediaPlayer.isPlaying()) {
                                miniPlay.setImageResource(R.drawable.ic_pause);
                            } else {
                                miniPlay.setImageResource(R.drawable.ic_pause);
                            }
                            playAudio(pos);
                        }
                    } else {
                        miniName.setText(audioModel.get(pos).getaName());
                        miniArtist.setText(audioModel.get(pos).getaArtist());
                        metaData(audioModel.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        }
                        playAudio(pos);
                    }
                } else {
                    if(!mediaPlayer.isPlaying()) {
                        pos = 0;
                        miniName.setText(audioModel.get(pos).getaName());
                        miniArtist.setText(audioModel.get(pos).getaArtist());
                        metaData(audioModel.get(pos).getaPath());
                        if (mediaPlayer.isPlaying()) {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        } else {
                            miniPlay.setImageResource(R.drawable.ic_pause);
                        }
                        playAudio(pos);
                    }
                }
                tempPosition = pos;
            }
        });
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    @Override
    public void onSongClick(int position) {
        final String TAG = "Song";
        AlbumDisplay.playerCheck = false;
        Log.d(TAG, "Clicked: " + position);
        isAlbum = false;
        pos = position;
        if(update){
            isUpdate = true;
            AudioModel currentAlbum = keepList.get(position);
            miniName.setText(keepList.get(position).getaName());
            miniArtist.setText(keepList.get(position).getaArtist());
            miniDur.setText(timerConversion(dur));
            metaData(keepList.get(position).getaPath());
            playAudio(position);
        } else {
            isUpdate = false;
            AudioModel currentAlbum = audioModel.get(position);
            byte[] image = getAlbumArt(currentAlbum.getaPath());
            miniName.setText(audioModel.get(position).getaName());
            miniArtist.setText(audioModel.get(position).getaArtist());
            miniDur.setText(timerConversion(dur));
            metaData(audioModel.get(position).getaPath());
            playAudio(position);
        }

        playStop();
        prevAudio(position);
        nextAudio(position);
        onCompleted(position);
    }

    public static ArrayList<AudioModel> getList() {
        return audioModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mediaPlayer.isPlaying()) {
            setAudioProgress();
            seek();
            nextAudio(pos);
            prevAudio(pos);
            playStop();
            onCompleted(pos);
            AudioModel currentAlbum = metaData.get(pos);
            byte[] image = getAlbumArt(currentAlbum.getaPath());
            if(isAlbum){
                miniName.setText(albumMetaData.get(pos).getaName());
                miniArtist.setText(albumMetaData.get(pos).getaArtist());
                miniDur.setText(timerConversion(dur));
                metaData(albumMetaData.get(pos).getaPath());
                miniPlay.setImageResource(R.drawable.ic_pause);
            } else {
                miniName.setText(metaData.get(pos).getaName());
                miniArtist.setText(metaData.get(pos).getaArtist());
                miniDur.setText(timerConversion(dur));
                metaData(metaData.get(pos).getaPath());
                miniPlay.setImageResource(R.drawable.ic_pause);
            }
        }
    }
}
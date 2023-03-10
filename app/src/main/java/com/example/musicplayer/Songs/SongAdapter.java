package com.example.musicplayer.Songs;


import static com.example.musicplayer.MainActivity.keepList;

import android.content.ContentProvider;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.PrecomputedTextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.AudioModel;
import com.example.musicplayer.R;
import com.google.android.exoplayer2.extractor.ts.H263Reader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.Format;
import java.util.ArrayList;

import timber.log.Timber;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    public static ArrayList <AudioModel> metaData;
    private Context context;
    OnSongListener mOnsongListener;
    public static Boolean update = false;

    public SongAdapter(Context context, ArrayList <AudioModel> metadata, OnSongListener listener)
    {
        this.context = context;
        metaData = metadata;
        mOnsongListener = listener;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        final OnSongListener songListener;

        public SongViewHolder(@NonNull View itemView, OnSongListener onSongListener) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.ivImg);
            mTextView1 = (TextView) itemView.findViewById(R.id.tvName);
            mTextView2 = (TextView) itemView.findViewById(R.id.tvArtist);
            mTextView3 = (TextView) itemView.findViewById(R.id.tvDur);
            this.songListener = onSongListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            songListener.onSongClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.album_items2, parent, false);
        SongViewHolder evh = new SongViewHolder(v, mOnsongListener);
        return evh;
    }


    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        long startTime = System.currentTimeMillis();
        setData(holder, position);
        Log.i("yes", "Bindview Time: " + (System.currentTimeMillis() - startTime));
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

    public void setData(SongViewHolder holder, int pos) {
        //timerConversion(metaData.get(pos).getaDuration()
        RequestOptions reqOpt = RequestOptions
                .fitCenterTransform()
                .transform(new RoundedCorners(55))
                .placeholder(R.drawable.ic_album) // It will cache your image after loaded for first time
                .override(holder.mImageView.getWidth(),holder.mImageView.getHeight());
        //Glide.with(context).clear(holder.mImageView);
        Glide.with(this.context).load(getAlbumArt(metaData.get(pos).getaPath())).apply(reqOpt).into(holder.mImageView);
        holder.mTextView3.setText(timerConversion(metaData.get(pos).getaDuration()));
        holder.mTextView1.setText(metaData.get(pos).getaName());
        holder.mTextView2.setText(metaData.get(pos).getaArtist());
        //holder.mImageView.setImageResource(R.drawable.ic_album);
        holder.mImageView.setPadding(0,0,0,0);
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();

        retriever.release();
        return art;
    }

    @Override
    public int getItemCount() {
        return metaData.size();
    }

    @Override
    public long getItemId(int position) {
        AudioModel feedItem = metaData.get(position);
        // Lets return in real stable id from here
        return feedItem.getId();
    }

    public interface OnSongListener {
        void onSongClick(int position);

    }

    public void updateList(ArrayList <AudioModel> musicFilesArrayList){
        metaData = new ArrayList<>();
        metaData.addAll(musicFilesArrayList);
        keepList = new ArrayList<>();
        keepList.addAll(musicFilesArrayList);
        notifyDataSetChanged();
        update = true;
    }

    @Override
    public void onViewRecycled(@NonNull SongViewHolder holder) {
        Glide.with(context).clear(holder.mImageView);
        super.onViewRecycled(holder);
    }
}
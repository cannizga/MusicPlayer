package com.example.musicplayer.Albums;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.text.PrecomputedText;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.PrecomputedTextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.AudioModel;
import com.example.musicplayer.R;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private OnAlbumListener mOnAlbumListener;
    public static ArrayList <AudioModel> albumMetaData;
    public static AudioModel currentAlbum;
    public static Context context;
    byte[] image;
    final RequestOptions myOptions = new RequestOptions()
            .centerCrop();

    public AlbumAdapter(Context context, ArrayList <AudioModel> data, OnAlbumListener onAlbumListener)
    {
        AlbumAdapter.context = context;
        albumMetaData = data;
        this.mOnAlbumListener = onAlbumListener;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView img;
        public TextView text1;
        public TextView text2;
        OnAlbumListener onAlbumListener;

        public AlbumViewHolder(View itemView, OnAlbumListener onAlbumListener) {
            super(itemView);
            img = itemView.findViewById(R.id.ivAlbum);
            text1 = itemView.findViewById(R.id.tvAlbumName);
            text2 = itemView.findViewById(R.id.tvAlbumArtist);
            Glide.with(context).clear(itemView);
            this.onAlbumListener = onAlbumListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onAlbumListener.onAlbumClick(getAdapterPosition());
        }
    }

    public AlbumAdapter (OnAlbumListener onAlbumListener) {
        this.mOnAlbumListener = onAlbumListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.album_cards, parent, false);
        AlbumViewHolder ev = new AlbumViewHolder(v, mOnAlbumListener);

        return ev;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        setData(holder, position);
    }

    public void setData(AlbumViewHolder holder, int pos) {
        currentAlbum = albumMetaData.get(pos);
        image = getAlbumArt(currentAlbum.getaPath());
        holder.text1.setText(currentAlbum.getaAlbum());
        holder.text2.setText(currentAlbum.getaArtist());
        Glide.with(context)
                .load(image)
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .encodeQuality(0)
                .frame(1)
                .priority(Priority.HIGH)
                .disallowHardwareConfig()
                .override(450,450)
                .error(R.drawable.ic_album)
                .skipMemoryCache(false)
                .centerCrop()
                .dontAnimate()
                .apply(myOptions)
                .into(holder.img);
        holder.img.setPadding(0,0,0,0);
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        } catch (Exception e) {
            e.printStackTrace();
        }
        retriever.release();
        return null;
    }

    @Override
    public int getItemCount() {
        return albumMetaData.size();
    }

    @Override
    public long getItemId(int position) {
        AudioModel feedItem = albumMetaData.get(position);
        // Lets return in real stable id from here
        return feedItem.getId();
    }

    public interface OnAlbumListener {
        void onAlbumClick(int position);

    }

}

package com.example.musicplayer.Albums.AlbumDisplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.musicplayer.AudioModel;
import com.example.musicplayer.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AlbumDisplayAdapter extends RecyclerView.Adapter<AlbumDisplayAdapter.AlbumViewHolder> {
    private OnAlbumListener mOnAlbumListener;
    public static ArrayList <AudioModel> albumMetaData;
    private Context context;
    long dura;

    public AlbumDisplayAdapter(Context context, ArrayList <AudioModel> data, OnAlbumListener onAlbumListener) {
        this.context = context;
        albumMetaData = data;
        this.mOnAlbumListener = onAlbumListener;
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView img;
        public TextView text1;
        public TextView text2;
        public TextView text3;
        public TextView nbr;
        final OnAlbumListener onAlbumListener;

        public AlbumViewHolder(@NonNull View itemView, OnAlbumListener onAlbumListener) {
            super(itemView);
            img = itemView.findViewById(R.id.ivImg);
            text1 = itemView.findViewById(R.id.tvName);
            text2 = itemView.findViewById(R.id.tvArtist);
            text3 = itemView.findViewById(R.id.tvDur);
            nbr = itemView.findViewById(R.id.tvCount);
            this.onAlbumListener = onAlbumListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onAlbumListener.onAlbumClick(getAdapterPosition());
        }
    }

    public AlbumDisplayAdapter(OnAlbumListener onAlbumListener) {
        this.mOnAlbumListener = onAlbumListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.songs_alt_items, parent, false);
        AlbumViewHolder ev = new AlbumViewHolder(v, mOnAlbumListener);
        return ev;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        AudioModel currentAlbum = albumMetaData.get(position);

        byte[] image = getAlbumArt(currentAlbum.getaPath());
        holder.text1.setText(currentAlbum.getaName());
        holder.text2.setText(currentAlbum.getaArtist());
        holder.nbr.setText(currentAlbum.getTrackNbr());
        dura = currentAlbum.getaDuration();
        holder.text3.setText(timerConversion(dura));

        if(image != null)
        {
            Glide.with(context).asBitmap().load(image).apply(RequestOptions.circleCropTransform()).into(holder.img);
        }
        else
        {
            Glide.with(context).load(R.drawable.ic_album).into(holder.img);
        }

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

    @Override
    public int getItemCount() {
        return albumMetaData.size();
    }

    public interface OnAlbumListener {
        void onAlbumClick(int position);

    }

}

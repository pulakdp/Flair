package com.flairmusicplayer.flair.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.customviews.SquareImageView;
import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.utils.FlairUtils;
import com.flairmusicplayer.flair.utils.NavUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class AlbumAdapter extends FastScrollRecyclerView.Adapter<AlbumAdapter.AlbumItemViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    public ArrayList<Album> albumList = new ArrayList<>();
    private AppCompatActivity activity;

    public AlbumAdapter(AppCompatActivity activity, ArrayList<Album> albumList) {
        this.activity = activity;
        this.albumList = albumList;
    }

    @Override
    public AlbumAdapter.AlbumItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(activity).inflate(R.layout.grid_item_album, parent, false);
        return new AlbumItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlbumAdapter.AlbumItemViewHolder holder, int position) {
        String albumName = albumList.get(position).getAlbumName();
        holder.albumName.setText(albumName);
        holder.albumDetail.setText(albumList.get(position).getArtistName());
        final Drawable textDrawable = FlairUtils.getRectTextDrawable(activity, albumName);
        Glide.with(activity)
                .load(Song.getAlbumArtUri(albumList.get(position).getAlbumId()))
                .apply(new RequestOptions().error(textDrawable))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.footerView.setBackgroundColor(activity.getResources().getColor(R.color.letter_tile_default_color));
                        holder.albumName.setTextColor(Color.BLACK);
                        holder.albumDetail.setTextColor(Color.BLACK);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap albumArt = FlairUtils.getBitmapFromDrawable(resource);
                        Palette.from(albumArt).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch swatch = palette.getVibrantSwatch();
                                if (swatch != null) {
                                    int vibrantColor = swatch.getRgb();
                                    int textColor = FlairUtils.getBlackWhiteColor(vibrantColor);
                                    holder.footerView.setBackgroundColor(vibrantColor);
                                    holder.albumName.setTextColor(textColor);
                                    holder.albumDetail.setTextColor(textColor);
                                } else {
                                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                    if (mutedSwatch != null) {
                                        int mutedColor = mutedSwatch.getRgb();
                                        int textColor = FlairUtils.getBlackWhiteColor(mutedColor);
                                        holder.footerView.setBackgroundColor(mutedColor);
                                        holder.albumName.setTextColor(textColor);
                                        holder.albumDetail.setTextColor(textColor);
                                    }
                                }
                            }
                        });
                        return false;
                    }
                })
                .into(holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return albumList != null ? albumList.size() : 0;
    }

    public void setData(ArrayList<Album> albums) {
        albumList = albums;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(albumList.get(position).getAlbumName().charAt(0)).toUpperCase();
    }

    public class AlbumItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.album_art)
        SquareImageView albumArt;

        @BindView(R.id.album_name)
        TextView albumName;

        @BindView(R.id.album_detail)
        TextView albumDetail;

        @BindView(R.id.album_footer)
        View footerView;

        public AlbumItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavUtils.goToAlbum(activity, albumList.get(getAdapterPosition()), albumArt);
        }
    }
}

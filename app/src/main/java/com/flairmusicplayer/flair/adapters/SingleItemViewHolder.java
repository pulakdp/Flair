package com.flairmusicplayer.flair.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flairmusicplayer.flair.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class SingleItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    @Nullable
    @BindView(R.id.item_image)
    public ImageView itemImage;

    @Nullable
    @BindView(R.id.item_image_text)
    public TextView itemImageText;

    @Nullable
    @BindView(R.id.item_title)
    public TextView itemTitle;

    @Nullable
    @BindView(R.id.item_detail_text)
    public TextView itemDetailText;

    @Nullable
    @BindView(R.id.item_menu)
    public ImageButton menu;


    public SingleItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}

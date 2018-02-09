package com.flairmusicplayer.flair.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.utils.MusicUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: PulakDebasish
 */

public class AlbumArtPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Song> playingQueue;

    public AlbumArtPagerAdapter(FragmentManager fragmentManager, ArrayList<Song> playingQueue) {
        super(fragmentManager);
        this.playingQueue = playingQueue;
    }

    @Override
    public Fragment getItem(int position) {
        return AlbumArtFragment.newInstance(playingQueue.get(position));
    }

    @Override
    public int getCount() {
        return playingQueue.size();
    }

    public static class AlbumArtFragment extends Fragment {

        public static final String SONG_ARG = "song";
        private Unbinder unbinder;

        private Song song;

        @BindView(R.id.player_album_art)
        ImageView albumArtView;

        public static AlbumArtFragment newInstance(final Song song) {
            Bundle args = new Bundle();
            args.putParcelable(SONG_ARG, song);
            AlbumArtFragment fragment = new AlbumArtFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            song = getArguments().getParcelable(SONG_ARG);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_album_art, container, false);
            unbinder = ButterKnife.bind(this, rootView);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            loadAlbumArt();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }

        private void loadAlbumArt() {
            Glide.with(getContext())
                    .load(MusicUtils.getAlbumArtUri(song.getAlbumId()))
                    .apply(new RequestOptions().error(R.drawable.album_art_placeholder))
                    .into(albumArtView);
        }
    }
}

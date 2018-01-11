package com.flairmusicplayer.flair.loaders;


import android.content.Context;
import android.text.TextUtils;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.models.Album;
import com.flairmusicplayer.flair.models.Artist;
import com.flairmusicplayer.flair.models.Song;

import java.util.ArrayList;

/**
 * Author: PulakDebasish
 */

public class SearchResultsLoader extends WrappedAsyncTaskLoader<ArrayList<Object>> {

    private String queryString;

    public SearchResultsLoader(Context context, String queryString) {
        super(context);
        this.queryString = queryString != null ? queryString.trim() : queryString;
    }

    @Override
    public ArrayList<Object> loadInBackground() {
        ArrayList<Object> resultList = new ArrayList<>();
        if (!TextUtils.isEmpty(queryString)) {
            ArrayList<Song> songs = SongLoader.getSongs(getContext(), queryString);
            if (!songs.isEmpty()) {
                resultList.add(getContext().getResources().getString(R.string.tab_songs));
                resultList.addAll(songs);
            }

            ArrayList<Artist> artists = ArtistLoader.getArtists(getContext(), queryString);
            if (!artists.isEmpty()) {
                resultList.add(getContext().getResources().getString(R.string.tab_artists));
                resultList.addAll(artists);
            }

            ArrayList<Album> albums = AlbumLoader.getAlbums(getContext(), queryString);
            if (!albums.isEmpty()) {
                resultList.add(getContext().getResources().getString(R.string.tab_albums));
                resultList.addAll(albums);
            }
        }
        return resultList;
    }
}

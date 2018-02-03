package com.flairmusicplayer.flair.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.loaders.SongLoader;
import com.flairmusicplayer.flair.models.Song;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.utils.FlairUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: PulakDebasish
 */

public class SongFileAdapter extends RecyclerView.Adapter<SongFileAdapter.SongFileViewHolder> {

    private AppCompatActivity activity;
    private FileSelectedListener listener;

    private List<File> files;
    private ArrayList<Song> songs;

    public SongFileAdapter(AppCompatActivity activity, List<File> files, FileSelectedListener listener) {
        this.activity = activity;
        this.files = files;
        this.listener = listener;
        songs = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private void listSongs() {
        new ListSongsAsyncTask(activity, new ListSongsAsyncTask.OnSongsListedCallback() {
            @Override
            public void onSongsListed(@NonNull ArrayList<Song> list) {
                songs = list;
            }
        }).execute(this.files);
    }

    public void setData(List<File> files) {
        this.files = files;
        listSongs();
        notifyDataSetChanged();
    }

    @Override
    public SongFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_single, parent, false);
        return new SongFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongFileViewHolder holder, int position) {
        final File file = files.get(position);

        if (holder.itemTitle != null) {
            holder.itemTitle.setText(getFileTitle(file));
        }

        if (holder.itemImage != null) {
            loadFileImage(file, holder);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void loadFileImage(File file, SongFileViewHolder holder) {
        if (file.isDirectory()) {
            holder.itemImage.setImageResource(R.drawable.ic_folder_black_24dp);
        } else {
            Glide.with(activity)
                    .load(Song
                            .getAlbumArtUri(SongLoader
                                    .getSongFromPath(activity, file.getAbsolutePath())
                                    .getAlbumId()))
                    .apply(new RequestOptions()
                            .error(R.drawable.ic_music_note_black_24dp))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(new RequestOptions()
                            .placeholder(FlairUtils.getRoundTextDrawable(activity, getFileTitle(file))))
                    .into(holder.itemImage);
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    protected String getFileTitle(File file) {
        return file.getName();
    }

    public interface FileSelectedListener {
        void onFileSelected(File file);
    }

    public static class ListSongsAsyncTask extends AsyncTask<List<File>, Void, ArrayList<Song>> {

        private WeakReference<Context> contextWeakReference;
        private WeakReference<OnSongsListedCallback> callbackWeakReference;

        private ListSongsAsyncTask(Context context, OnSongsListedCallback onSongsListedCallback) {
            contextWeakReference = new WeakReference<>(context);
            callbackWeakReference = new WeakReference<>(onSongsListedCallback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            checkContextReference();
            checkCallbackReference();
        }

        @Override
        protected ArrayList<Song> doInBackground(List<File>... params) {
            if (checkContextReference() != null)
                return SongLoader.getSongsForFiles(contextWeakReference.get(), params[0]);
            else
                return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            super.onPostExecute(songs);
            OnSongsListedCallback callback = checkCallbackReference();
            if (songs != null && callback != null && !songs.isEmpty())
                callback.onSongsListed(songs);
        }

        private Context checkContextReference() {
            Context context = contextWeakReference.get();
            if (context == null) {
                cancel(false);
            }
            return context;
        }

        private OnSongsListedCallback checkCallbackReference() {
            OnSongsListedCallback callback = callbackWeakReference.get();
            if (callback == null) {
                cancel(false);
            }
            return callback;
        }

        public interface OnSongsListedCallback {
            void onSongsListed(@NonNull ArrayList<Song> songs);
        }
    }

    public class SongFileViewHolder extends SingleItemViewHolder {
        public SongFileViewHolder(View itemView) {
            super(itemView);
            if (itemDetailText != null)
                itemDetailText.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            final File file = files.get(getAdapterPosition());
            if (file.isDirectory()) {
                listener.onFileSelected(files.get(getAdapterPosition()));
            } else if (file.isFile()) {
                FlairMusicController.openQueue(songs, getAdapterPosition(), true);
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        int current = -1;
//                        long songId = SongLoader.getSongFromPath(activity,
//                                file.getAbsolutePath()).getId();
//
//                        ArrayList<Song> songList = new ArrayList<>(songs.size());
//                        int j = 0;
//                        for (int i = 0; i < getItemCount(); i++) {
//                            if (songs.get(i).getId() != -1) {
//                                songList.add(songs.get(i));
//                                if (songs.get(i).getId() == songId) {
//                                    current = j;
//                                }
//                                j++;
//                            }
//                        }
//                        FlairMusicController.openQueue(songList, current, true);
//
//
//                    }
//                }, 100);
            }
        }
    }
}

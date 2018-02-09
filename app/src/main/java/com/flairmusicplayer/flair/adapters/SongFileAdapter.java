package com.flairmusicplayer.flair.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
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
import com.flairmusicplayer.flair.utils.MusicUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

/**
 * Author: PulakDebasish
 */

public class SongFileAdapter extends RecyclerView.Adapter<SongFileAdapter.SongFileViewHolder> {

    private AppCompatActivity activity;
    private FileSelectedListener listener;
    private OnEmptyFolderCallback emptyFolderCallback;
    private ListSongsAsyncTask listSongsAsyncTask;

    private List<File> files = new LinkedList<>();
    private ArrayList<Song> songs = new ArrayList<>();

    public SongFileAdapter(AppCompatActivity activity,
                           FileSelectedListener listener,
                           OnEmptyFolderCallback emptyFolderCallback) {
        this.activity = activity;
        this.listener = listener;
        this.emptyFolderCallback = emptyFolderCallback;
    }

    @SuppressWarnings("unchecked")
    private void listSongsAsync(List<File> files) {
        if (listSongsAsyncTask != null)
            listSongsAsyncTask.cancel(true);
        listSongsAsyncTask = new ListSongsAsyncTask(activity);
        listSongsAsyncTask.execute(files);
    }

    public void setData(List<File> files) {
        listSongsAsync(files);
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
                    .load(MusicUtils
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

    public interface OnEmptyFolderCallback {
        void onEmptyFolder();
    }

    public interface FileSelectedListener {
        void onFileSelected(File file);
    }

    public class ListSongsAsyncTask extends AsyncTask<List<File>, Void, ArrayList<Song>> {

        private WeakReference<Context> contextWeakReference;
        private List<File> fileList = new LinkedList<>();

        private ListSongsAsyncTask(Context context) {
            contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            checkContextReference();
            files.clear();
            songs.clear();
            notifyDataSetChanged();
        }

        @Override
        protected ArrayList<Song> doInBackground(List<File>... params) {
            fileList = params[0];
            if (checkContextReference() != null) {
                ArrayList<Song> songList = SongLoader.getSongsForFiles(contextWeakReference.get(), params[0]);
                if (!isCancelled())
                    return songList;
                else
                    return new ArrayList<>();
            } else
                return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songList) {
            super.onPostExecute(songList);
            if (isCancelled())
                return;
            files.addAll(fileList);
            songs.addAll(songList);
            notifyDataSetChanged();
            if (files.isEmpty() && songs.isEmpty()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        emptyFolderCallback.onEmptyFolder();
                    }
                }, 100);
            }
        }

        private Context checkContextReference() {
            Context context = contextWeakReference.get();
            if (context == null) {
                cancel(false);
            }
            return context;
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
            Timber.d("onClick at %d", getAdapterPosition());
            final File file = files.get(getAdapterPosition());
            if (file.isDirectory()) {
                listener.onFileSelected(files.get(getAdapterPosition()));
            } else if (file.isFile()) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int current = -1;
                        long songId = SongLoader.getSongFromPath(activity,
                                file.getAbsolutePath()).getId();

                        Timber.d("Song id: %s", songId);

                        int j = 0;
                        for (int i = 0; i < songs.size(); i++) {
                            if (songs.get(i).getId() != -1) {
                                if (songs.get(i).getId() == songId) {
                                    current = j;
                                }
                                j++;
                            }
                        }
                        Timber.d("Values: List size = %d, Current pos = %d", songs.size(), current);
                        FlairMusicController.openQueue(songs, current, true);
                    }
                }, 100);
            }
        }
    }
}

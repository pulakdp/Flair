package com.flairmusicplayer.flair.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.SongFileAdapter;
import com.flairmusicplayer.flair.customviews.BreadCrumbLayout;
import com.flairmusicplayer.flair.loaders.FileLoader;
import com.flairmusicplayer.flair.ui.activities.MainActivity;
import com.flairmusicplayer.flair.utils.FileUtils;
import com.flairmusicplayer.flair.utils.PreferenceUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Author: PulakDebasish
 */

public class FoldersFragment extends MusicServiceFragment
        implements BreadCrumbLayout.SelectionCallback,
        SongFileAdapter.FileSelectedListener,
        LoaderManager.LoaderCallbacks<List<File>>,
        SongFileAdapter.OnEmptyFolderCallback {

    private static final String PATH = "path";
    private static final String CRUMBS = "crumbs";
    private static final int LOADER_ID = 6;

    @BindView(R.id.folder_toolbar)
    Toolbar toolbar;

    @BindView(R.id.bread_crumbs)
    BreadCrumbLayout breadCrumbLayout;

    @BindView(R.id.folder_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.empty_text)
    TextView emptyText;

    @BindView(R.id.folder_loading_bar)
    ProgressBar progressBar;

    private SongFileAdapter adapter;

    public FoldersFragment() {
    }

    public static FoldersFragment newInstance(Context context) {
        return newInstance(PreferenceUtils.getInstance(context).getLastOpenedDirectory());
    }

    public static FoldersFragment newInstance(File directory) {
        FoldersFragment frag = new FoldersFragment();
        Bundle b = new Bundle();
        b.putSerializable(PATH, directory);
        frag.setArguments(b);
        return frag;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            setCrumb(new BreadCrumbLayout.Crumb((File) getArguments().getSerializable(PATH)),
                    true);
        } else {
            breadCrumbLayout.restoreFromStateWrapper((BreadCrumbLayout.SavedStateWrapper)
                    savedInstanceState.getParcelable(CRUMBS));
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CRUMBS, breadCrumbLayout.getStateWrapper());
    }

    public void setCrumb(BreadCrumbLayout.Crumb crumb, boolean addToHistory) {
        if (crumb == null) return;
        saveScrollPosition();
        breadCrumbLayout.setActiveOrAdd(crumb, false);
        if (addToHistory) {
            breadCrumbLayout.addHistory(crumb);
        }
        emptyText.setVisibility(View.GONE);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void saveScrollPosition() {
        BreadCrumbLayout.Crumb crumb = getActiveCrumb();
        if (crumb != null) {
            crumb.setScrollPosition(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setUpToolbar();
        setUpBreadCrumbs();
        setUpAdapter();
        setUpRecyclerView();
    }

    private void setUpToolbar() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.addDrawerToggle(toolbar);
        }
    }

    private void setUpBreadCrumbs() {
        breadCrumbLayout.setCallback(this);
    }

    private void setUpAdapter() {
        adapter = new SongFileAdapter((AppCompatActivity) getActivity(),
                this,
                this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                progressBar.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    public BreadCrumbLayout.Crumb getActiveCrumb() {
        return breadCrumbLayout.size() > 0 ? breadCrumbLayout.getCrumb(breadCrumbLayout.getActiveIndex()) : null;
    }

    @Override
    public void onCrumbSelection(BreadCrumbLayout.Crumb crumb, int index) {
        setCrumb(crumb, true);
    }

    @Override
    public void onFileSelected(File file) {
        if (file.isDirectory()) {
            setCrumb(new BreadCrumbLayout.Crumb(file), true);
        }
    }

    public Comparator<File> getFileComparator() {
        return new FileComparator();
    }

    public FileFilter getFileFilter() {
        return new AudioFileFilter();
    }

    private void updateAdapter(@NonNull List<File> files) {
        adapter.setData(files);
        BreadCrumbLayout.Crumb crumb = getActiveCrumb();
        if (crumb != null) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(crumb.getScrollPosition(), 0);
        }
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        return new FileLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        updateAdapter(data);
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        updateAdapter(new LinkedList<File>());
    }

    @Override
    public void onEmptyFolder() {
        Timber.d("onEmptyFolder called");
        progressBar.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }

    private static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                return f1.getName().compareToIgnoreCase
                        (f2.getName());
            }
        }
    }

    private static class AudioFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            return !file.isHidden() && (file.isDirectory() ||
                    FileUtils.fileIsMimeType(file, "audio/*", MimeTypeMap.getSingleton()) ||
                    FileUtils.fileIsMimeType(file, "application/ogg", MimeTypeMap.getSingleton()));
        }
    }
}

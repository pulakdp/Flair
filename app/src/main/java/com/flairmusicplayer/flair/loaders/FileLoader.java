package com.flairmusicplayer.flair.loaders;

import android.support.annotation.Nullable;

import com.flairmusicplayer.flair.customviews.BreadCrumbLayout;
import com.flairmusicplayer.flair.ui.fragments.FoldersFragment;
import com.flairmusicplayer.flair.utils.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: PulakDebasish
 */

public class FileLoader extends WrappedAsyncTaskLoader<List<File>> {

    private WeakReference<FoldersFragment> fragmentWeakReference;

    public FileLoader(FoldersFragment foldersFragment) {
        super(foldersFragment.getActivity());
        fragmentWeakReference = new WeakReference<>(foldersFragment);
    }

    @Nullable
    @Override
    public List<File> loadInBackground() {
        FoldersFragment fragment = fragmentWeakReference.get();
        File directory = null;
        if (fragment != null) {
            BreadCrumbLayout.Crumb crumb = fragment.getActiveCrumb();
            if (crumb != null) {
                directory = crumb.getFile();
            }
        }
        if (directory != null) {
            List<File> files = FileUtils.listFiles(directory, fragment.getFileFilter());
            Collections.sort(files, fragment.getFileComparator());
            return files;
        } else {
            return new LinkedList<>();
        }
    }
}

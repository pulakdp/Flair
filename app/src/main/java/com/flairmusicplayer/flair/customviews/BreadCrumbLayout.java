package com.flairmusicplayer.flair.customviews;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flairmusicplayer.flair.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: PulakDebasish, kabouzeid
 */

public class BreadCrumbLayout extends HorizontalScrollView implements View.OnClickListener {

    private List<Crumb> currentCrumbs;
    private List<Crumb> oldCrumbs;
    private List<Crumb> history;

    private LinearLayout childFrame;
    private SelectionCallback callback;
    private int active;

    public interface SelectionCallback {
        void onCrumbSelection(Crumb crumb, int index);
    }

    public void setCallback(SelectionCallback callback) {
        this.callback = callback;
    }

    public BreadCrumbLayout(Context context) {
        super(context);
        init();
    }

    public BreadCrumbLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BreadCrumbLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMinimumHeight((int) getResources().getDimension(R.dimen.tab_height));
        setClipToPadding(false);
        setHorizontalScrollBarEnabled(false);
        currentCrumbs = new ArrayList<>();
        history = new ArrayList<>();
        childFrame = new LinearLayout(getContext());
        addView(childFrame, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void addHistory(Crumb crumb) {
        history.add(crumb);
    }

    public void addCrumb(@NonNull Crumb crumb, boolean refreshLayout) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.bread_crumb, this, false);
        view.setTag(currentCrumbs.size());
        view.setOnClickListener(this);

        ImageView imageView = (ImageView) view.getChildAt(1);
        imageView.setVisibility(View.GONE);

        childFrame.addView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        currentCrumbs.add(crumb);
        if (refreshLayout) {
            active = currentCrumbs.size() - 1;
            requestLayout();
        }
        invalidateActivatedAll();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //RTL works fine like this
        View child = childFrame.getChildAt(active);
        if (child != null)
            smoothScrollTo(child.getLeft(), 0);
    }

    public void clearCrumbs() {
        try {
            oldCrumbs = new ArrayList<>(currentCrumbs);
            currentCrumbs.clear();
            childFrame.removeAllViews();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public Crumb getCrumb(int index) {
        return currentCrumbs.get(index);
    }

    private boolean setActive(Crumb newActive) {
        active = currentCrumbs.indexOf(newActive);
        invalidateActivatedAll();
        boolean success = active > -1;
        if (success)
            requestLayout();
        return success;
    }

    void invalidateActivatedAll() {
        for (int i = 0; i < currentCrumbs.size(); i++) {
            Crumb crumb = currentCrumbs.get(i);
            invalidateActivated(childFrame.getChildAt(i), active == currentCrumbs.indexOf(crumb), false, i < currentCrumbs.size() - 1).setText(crumb.getTitle());
        }
    }

    public void setActiveOrAdd(@NonNull Crumb crumb, boolean forceRecreate) {
        if (forceRecreate || !setActive(crumb)) {
            clearCrumbs();
            final List<File> newPathSet = new ArrayList<>();

            newPathSet.add(0, crumb.getFile());

            File p = crumb.getFile();
            while ((p = p.getParentFile()) != null) {
                newPathSet.add(0, p);
            }

            for (int index = 0; index < newPathSet.size(); index++) {
                final File fi = newPathSet.get(index);
                crumb = new Crumb(fi);

                // Restore scroll positions saved before clearing
                if (oldCrumbs != null) {
                    for (Iterator<Crumb> iterator = oldCrumbs.iterator(); iterator.hasNext(); ) {
                        Crumb old = iterator.next();
                        if (old.equals(crumb)) {
                            crumb.setScrollPosition(old.getScrollPosition());
                            iterator.remove(); // minimize number of linear passes by removing un-used crumbs from history
                            break;
                        }
                    }
                }
                addCrumb(crumb, true);
            }
            // History no longer needed
            oldCrumbs = null;
        }
    }

    public int size() {
        return currentCrumbs.size();
    }

    private TextView invalidateActivated(View view, final boolean isActive, final boolean noArrowIfAlone, final boolean allowArrowVisible) {
        LinearLayout child = (LinearLayout) view;
        TextView textView = (TextView) child.getChildAt(0);
        ImageView imageView = (ImageView) child.getChildAt(1);
        if (noArrowIfAlone && getChildCount() == 1)
            imageView.setVisibility(View.GONE);
        else if (allowArrowVisible)
            imageView.setVisibility(View.VISIBLE);
        else
            imageView.setVisibility(View.GONE);
        return textView;
    }

    public int getActiveIndex() {
        return active;
    }

    @Override
    public void onClick(View v) {
        if (callback != null) {
            int index = (Integer) v.getTag();
            callback.onCrumbSelection(currentCrumbs.get(index), index);
        }
    }

    public static class SavedStateWrapper implements Parcelable {

        public final int active;
        public final List<Crumb> crumbs;
        public final int visibility;

        public SavedStateWrapper(BreadCrumbLayout view) {
            active = view.active;
            crumbs = view.currentCrumbs;
            visibility = view.getVisibility();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.active);
            dest.writeTypedList(crumbs);
            dest.writeInt(this.visibility);
        }

        protected SavedStateWrapper(Parcel in) {
            this.active = in.readInt();
            this.crumbs = in.createTypedArrayList(Crumb.CREATOR);
            this.visibility = in.readInt();
        }

        public static final Creator<SavedStateWrapper> CREATOR = new Creator<SavedStateWrapper>() {
            public SavedStateWrapper createFromParcel(Parcel source) {
                return new SavedStateWrapper(source);
            }

            public SavedStateWrapper[] newArray(int size) {
                return new SavedStateWrapper[size];
            }
        };
    }

    public SavedStateWrapper getStateWrapper() {
        return new SavedStateWrapper(this);
    }

    public void restoreFromStateWrapper(SavedStateWrapper mSavedState) {
        if (mSavedState != null) {
            active = mSavedState.active;
            for (Crumb c : mSavedState.crumbs) {
                addCrumb(c, false);
            }
            requestLayout();
            setVisibility(mSavedState.visibility);
        }
    }

    public static class Crumb implements Parcelable {

        public static final String ROOT_DIR = "root";

        public Crumb(File file) {
            this.file = file;
        }

        private final File file;
        private int scrollPos;

        public int getScrollPosition() {
            return scrollPos;
        }

        public void setScrollPosition(int scrollY) {
            this.scrollPos = scrollY;
        }

        public String getTitle() {
            return file.getPath().equals("/") ? ROOT_DIR : file.getName();
        }

        public File getFile() {
            return file;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Crumb) && ((Crumb) o).getFile() != null &&
                    ((Crumb) o).getFile().equals(getFile());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSerializable(this.file);
            dest.writeInt(this.scrollPos);
        }

        protected Crumb(Parcel in) {
            this.file = (File) in.readSerializable();
            this.scrollPos = in.readInt();
        }

        public static final Creator<Crumb> CREATOR = new Creator<Crumb>() {
            @Override
            public Crumb createFromParcel(Parcel source) {
                return new Crumb(source);
            }

            @Override
            public Crumb[] newArray(int size) {
                return new Crumb[size];
            }
        };
    }

}

package com.flairmusicplayer.flair.ui.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.services.FlairMusicController;
import com.flairmusicplayer.flair.ui.fragments.MiniPlayerFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public abstract class SlidingPanelActivity extends MusicServiceActivity
        implements SlidingUpPanelLayout.PanelSlideListener {

    @BindView(R.id.slide_panel_layout)
    SlidingUpPanelLayout panel;

    private MiniPlayerFragment miniPlayerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createContentView());
        ButterKnife.bind(this);

        setUpMiniPlayer();
        setUpPanel();
    }

    private void setUpMiniPlayer() {
        miniPlayerFragment = (MiniPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.mini_player_fragment);
        //noinspection ConstantConditions
        miniPlayerFragment.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandPanel();
            }
        });
    }

    private void setUpPanel() {
        panel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                panel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    onPanelSlide(panel, 1);

                } else if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    onPanelSlide(panel, 0);
                }
            }
        });
        panel.addPanelSlideListener(this);
    }

    public void setPanelTouchEnabled(boolean touchEnabled) {
        panel.setTouchEnabled(touchEnabled);
    }

    public SlidingUpPanelLayout.PanelState getPanelState() {
        return panel != null ? panel.getPanelState() : null;
    }

    public void collapsePanel() {
        panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void expandPanel() {
        panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void onPanelExpanded() {

    }

    public void onPanelCollapsed() {

    }

    public void hideBottomBar(final boolean hide) {
        if (hide) {
            panel.setPanelHeight(0);
            collapsePanel();
        } else {
            panel.setPanelHeight(getResources().getDimensionPixelSize(R.dimen.mini_player_height));
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        setMiniPlayerAlpha(slideOffset);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        switch (newState) {
            case COLLAPSED:
                onPanelCollapsed();
                break;
            case EXPANDED:
                onPanelExpanded();
                break;
            case ANCHORED:
                break;
        }
    }

    public void setMiniPlayerAlpha(float progress) {
        if (miniPlayerFragment.getView() == null) return;
        float alpha = 1 - progress;
        miniPlayerFragment.getView().setAlpha(alpha);
        miniPlayerFragment.getView().setVisibility(alpha == 0 ? View.GONE : View.VISIBLE);
    }

    protected abstract View createContentView();

    protected View wrapSlidingPanel(@LayoutRes int resId) {
        View slidingPanelLayout = getLayoutInflater().inflate(R.layout.sliding_panel_layout, null);
        ViewGroup contentContainer = slidingPanelLayout.findViewById(R.id.content_container);
        getLayoutInflater().inflate(resId, contentContainer);
        return slidingPanelLayout;
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        if (!FlairMusicController.getPlayingQueue().isEmpty()) {
            hideBottomBar(false);
        }
    }

    @Override
    public void onQueueChanged() {
        super.onQueueChanged();
        hideBottomBar(FlairMusicController.getPlayingQueue().isEmpty());
    }
}

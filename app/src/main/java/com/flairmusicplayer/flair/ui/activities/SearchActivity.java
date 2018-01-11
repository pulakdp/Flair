package com.flairmusicplayer.flair.ui.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.flairmusicplayer.flair.R;
import com.flairmusicplayer.flair.adapters.SearchAdapter;
import com.flairmusicplayer.flair.loaders.SearchResultsLoader;
import com.flairmusicplayer.flair.utils.FlairUtils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: PulakDebasish
 */

public class SearchActivity extends MusicServiceActivity
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<ArrayList<Object>> {

    @BindView(R.id.search_toolbar)
    Toolbar toolbar;

    @BindView(R.id.search_results_view)
    RecyclerView searchResultsView;

    @BindView(R.id.empty_text)
    TextView emptyText;

    SearchView searchView;

    public static final int LOADER_ID = 4;
    public static final String QUERY_STRING = "queryString";
    private SearchAdapter adapter;
    private String queryString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setUpToolBar();

        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this, new ArrayList<>(Collections.emptyList()));
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                emptyText.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });
        searchResultsView.setAdapter(adapter);
        searchResultsView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });

        if (savedInstanceState != null)
            queryString = savedInstanceState.getString(QUERY_STRING);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void search(String queryString) {
        this.queryString = queryString;
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY_STRING, queryString);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        MenuItemCompat.expandActionView(searchItem);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return false;
            }
        });
        searchView.setQuery(queryString, false);
        searchView.setOnQueryTextListener(SearchActivity.this);

        return super.onCreateOptionsMenu(menu);
    }

    private void hideSoftKeyboard() {
        FlairUtils.hideSoftKeyboard(SearchActivity.this);
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        hideSoftKeyboard();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return false;
    }

    @Override
    public Loader<ArrayList<Object>> onCreateLoader(int i, Bundle bundle) {
        return new SearchResultsLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Object>> loader, ArrayList<Object> data) {
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Object>> loader) {
        adapter.setData(new ArrayList<>(Collections.emptyList()));
    }
}

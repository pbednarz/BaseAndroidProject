package pl.solaris.baseproject.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.solaris.baseproject.R;
import pl.solaris.baseproject.adapter.NavigationAdapter;
import pl.solaris.baseproject.adapter.OnItemClickListener;
import pl.solaris.baseproject.fragment.PhotoListFragment;

/**
 * PGS-Software
 * Created by pbednarz on 2015-02-03.
 */
public class MainActivity extends ActionBarActivity implements OnItemClickListener {

    private final String CURRENT_POSITION = "position";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.menu_drawer)
    RecyclerView menuRecycler;
    private int mPosition = -1;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] albums = {"Food", "Sport"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(20.0f);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                setTitle(albums[mPosition]);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                toolbar.setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        menuRecycler.setHasFixedSize(true);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        menuRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        menuRecycler.setAdapter(new NavigationAdapter(albums, this));
        menuRecycler.setItemAnimator(new DefaultItemAnimator());

        selectItem(savedInstanceState != null ?
                savedInstanceState.getInt(CURRENT_POSITION)
                : 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    public void selectItem(int position) {
        if (mPosition != position) {
            mPosition = position;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PhotoListFragment.newInstance(albums[position]))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            setTitle(albums[position]);
        }
        mDrawerLayout.closeDrawer(menuRecycler);
    }

    @Override
    public void onBackPressed() {
        if (mPosition != 0) {
            selectItem(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }
}

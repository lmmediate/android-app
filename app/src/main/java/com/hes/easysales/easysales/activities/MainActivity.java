package com.hes.easysales.easysales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.hes.easysales.easysales.FetchData;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.adapters.ItemAdapter;
import com.hes.easysales.easysales.fragments.FavoritesFragment;
import com.hes.easysales.easysales.fragments.HomeFragment;
import com.hes.easysales.easysales.fragments.ShopListFragment;
import com.hes.easysales.easysales.utilities.InternetUtil;

import java.util.ArrayList;

/**
 * Created by sinopsys on 2/18/18.
 */

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG_FRAGMENT_ONE = "fragment_one";
    private static final String TAG_FRAGMENT_TWO = "fragment_two";
    private static final String TAG_FRAGMENT_THREE = "fragment_three";
    public Parcelable layoutState;
    public ItemAdapter adapter;
    public ItemAdapter shopListAdapter;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        FrameLayout content = findViewById(R.id.fragmentContainer);

        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
        if (fragment == null) {
            fragment = HomeFragment.newInstance();
        }
        replaceFragment(fragment, TAG_FRAGMENT_TWO);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setOnNavigationItemReselectedListener(reselectNavListener);
        bottomNav.setSelectedItemId(R.id.nav_home);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .commit();


        if (InternetUtil.isConnectedToInternet(getApplicationContext())) {
            new FetchData(this, swipeRefreshLayout).execute();
        } else {
            Toast.makeText(this, R.string.noInternetToast, Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
        }

        adapter = new ItemAdapter(new ArrayList<Item>(), this);
    }

    private BottomNavigationView.OnNavigationItemReselectedListener reselectNavListener = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {
            // Scroll to top if reselected.
            //
            if (item.getItemId() == R.id.nav_home) {
                ((RecyclerView) findViewById(R.id.itemList)).scrollToPosition(0);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // Pull-to-update logic.
            //
            new FetchData(MainActivity.this, swipeRefreshLayout).execute();
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_favorites: {
                    Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ONE);
                    if (fragment == null) {
                        fragment = FavoritesFragment.newInstance();
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_ONE);
                    break;
                }
                case R.id.nav_home: {
                    Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
                    if (fragment == null) {
                        fragment = HomeFragment.newInstance();
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_TWO);
                    break;
                }
                case R.id.nav_shoplist: {
                    Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_THREE);
                    if (fragment == null) {
                        fragment = ShopListFragment.newInstance();
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_THREE);
                    break;
                }
            }
            return true;
        }
    };

    private void replaceFragment(@NonNull Fragment fragment, @NonNull String tag) {
        if (!fragment.equals(currentFragment)) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, tag)
                    .commit();
            currentFragment = fragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //
        getMenuInflater().inflate(R.menu.app_bar_items, menu);

        MenuItem item = menu.findItem(R.id.shops_spinner);
        Spinner spinner = (Spinner) item.getActionView();

        // TODO get shop names using api request.
        // TODO RV switch items for different shops (logic).
        //
        String[] shops = {getString(R.string.shop1), getString(R.string.shop2), getString(R.string.shop3)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, shops);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                //
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                //
                return super.onOptionsItemSelected(item);

        }
    }
}


// EOF

package com.hes.easysales.easysales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.fragments.FavoritesFragment;
import com.hes.easysales.easysales.fragments.HomeFragment;
import com.hes.easysales.easysales.fragments.ShopListFragment;
import com.hes.easysales.easysales.utilities.InternetUtil;
import com.viven.fragmentstatemanager.FragmentStateManager;

/**
 * Created by sinopsys on 2/18/18.
 */

public class MainActivity extends AppCompatActivity {
    FragmentStateManager fragmentStateManager;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        FrameLayout content = findViewById(R.id.fragmentContainer);
        fragmentStateManager = new FragmentStateManager(content, getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new FavoritesFragment();
                    case 1:
                        return new HomeFragment();
                    case 2:
                        return new ShopListFragment();
                    default:
                        return null;
                }
            }
        };

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
            int position = getNavPositionFromMenuItem(item);
            if (position != -1) {
                fragmentStateManager.changeFragment(getNavPositionFromMenuItem(item));
                return true;
            }

            return false;
        }
    };

    private int getNavPositionFromMenuItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_favorites:
                return 0;
            case R.id.nav_home:
                return 1;
            case R.id.nav_shoplist:
                return 2;
        }
        return -1;
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

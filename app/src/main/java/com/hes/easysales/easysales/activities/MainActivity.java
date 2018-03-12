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
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hes.easysales.easysales.FetchData;
import com.hes.easysales.easysales.fragments.FavoritesFragment;
import com.hes.easysales.easysales.fragments.HomeFragment;
import com.hes.easysales.easysales.fragments.ShopListFragment;
import com.hes.easysales.easysales.R;
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
                RecyclerView rv = findViewById(R.id.itemList);
                rv.scrollToPosition(0);
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
                case R.id.nav_favorites:
                    fragmentStateManager.changeFragment(0);
                    return true;
                case R.id.nav_home:
                    fragmentStateManager.changeFragment(1);
                    return true;
                case R.id.nav_shoplist:
                    fragmentStateManager.changeFragment(2);
                    return true;
            }
            return false;
        }
    };
}


// EOF

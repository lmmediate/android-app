package com.hes.easysales.easysales.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hes.easysales.easysales.FetchData;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.Shop;
import com.hes.easysales.easysales.ShopList;
import com.hes.easysales.easysales.adapters.ItemAdapter;
import com.hes.easysales.easysales.adapters.ShopListsPreviewAdapter;
import com.hes.easysales.easysales.fragments.FavoritesFragment;
import com.hes.easysales.easysales.fragments.HomeFragment;
import com.hes.easysales.easysales.fragments.ShopListsPreviewFragment;
import com.hes.easysales.easysales.utilities.InternetUtil;
import com.hes.easysales.easysales.utilities.ShopsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hes.easysales.easysales.Config.DIXY_SHOP_ID;
import static com.hes.easysales.easysales.Config.PEREKRESTOK_SHOP_ID;

/**
 * Created by sinopsys on 2/18/18.
 */

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG_FRAGMENT_ONE = "fragment_one";
    private static final String TAG_FRAGMENT_TWO = "fragment_two";
    private static final String TAG_FRAGMENT_THREE = "fragment_three";
    private Shop selectedShop = null;
    public Parcelable itemsFragmentState;
    public Parcelable shopListsPreviewFragmentState;
    public ItemAdapter adapter;
    public ShopListsPreviewAdapter shopListsPreviewAdapter;
    public FetchData fetchData;
    public List<Shop> shops;
    public RequestQueue queue;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        fetchData = new FetchData(this, swipeRefreshLayout);

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

        adapter = new ItemAdapter(new ArrayList<Item>(), this);
        shopListsPreviewAdapter = new ShopListsPreviewAdapter(new ArrayList<ShopList>(), this);
        shops = new ArrayList<>();
        queue = Volley.newRequestQueue(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((ProgressBar) findViewById(R.id.pbLoading)).setProgress(0, true);
        } else {
            ((ProgressBar) findViewById(R.id.pbLoading)).setProgress(0);
        }

        if (InternetUtil.isConnectedToInternet(getApplicationContext())) {
            fetchData.execute();
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
                int pos = ((LinearLayoutManager) (((RecyclerView) findViewById(R.id.itemList))
                        .getLayoutManager())).findFirstVisibleItemPosition();
                int offset = 10;
                if (pos > offset) {
                    ((RecyclerView) findViewById(R.id.itemList)).smoothScrollToPosition(pos - offset);
                }
                Handler mHandler = new Handler();
                Runnable codeToRun = new Runnable() {
                    @Override
                    public void run() {
                        ((RecyclerView) findViewById(R.id.itemList)).scrollToPosition(0);
                    }
                };
                mHandler.postDelayed(codeToRun, 200);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // Pull-to-update logic.
            //
            fetchData.execute();
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
                        fragment = ShopListsPreviewFragment.newInstance();
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
        // FIXME Find out how to wait untill volley response..
        //
        shops.add(new Shop(1, "dixy", "Дикси"));
        shops.add(new Shop(2, "perekrestok", "Перекресток"));
        getMenuInflater().inflate(R.menu.app_bar_items, menu);
        Spinner spinner = findViewById(R.id.spnrShopList);
        String[] shopsToShow = new String[shops.size() + 1];
        shopsToShow[0] = getString(R.string.all_shops);
        for (int i = 0; i < shops.size(); ++i) {
            shopsToShow[i + 1] = shops.get(i).getName();
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, shopsToShow);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedShop = null;
                    MainActivity.this.adapter.returnToOld();
                } else {
                    selectedShop = shops.get(position - 1);
                    MainActivity.this.adapter.filter("shopId", String.valueOf(selectedShop.getId()), false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainActivity.this.adapter.filter("name", query, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    MainActivity.this.adapter.filter("name", newText, false);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                //
//                List<String> query = new ArrayList<>();
//                query.add("Кофе, чай");
//                query.add("Наши марки");
//                adapter.new ComplexQuery("category", query).apply();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                //
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.doubleBackExit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            setResult(0);
            finish();
        }
    }

    public void filterSelectedShops() {
        if (selectedShop == null) {
            adapter.returnToOld();
        } else {
            adapter.filter("shopId", String.valueOf(selectedShop.getId()), false);
        }
    }
}


// EOF

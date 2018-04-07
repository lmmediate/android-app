package com.hes.easysales.easysales.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hes.easysales.easysales.APIRequests;
import com.hes.easysales.easysales.Config;
import com.hes.easysales.easysales.EndlessRCVScrollListener;
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
import com.hes.easysales.easysales.utilities.JSONUtil;
import com.hes.easysales.easysales.utilities.SharedPrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private View btnAdd;
    public boolean homeActive = true;
    public int currentPage = 1;
    public String selectedCategory = "";
    public int totalItemsCount;
    public Parcelable itemsFragmentState;
    public Parcelable shopListsPreviewFragmentState;
    public ItemAdapter adapter;
    public ShopListsPreviewAdapter shopListsPreviewAdapter;
    public FetchData fetchData;
    public List<Shop> shops;
    public RequestQueue queue;
    private FragmentManager fragmentManager;
    public List<String> categories;
    private int currentFragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        btnAdd = findViewById(R.id.action_add);
        btnAdd.setOnClickListener(addShopList);

        fetchData = new FetchData(this, swipeRefreshLayout);
        categories = new ArrayList<>();

        FrameLayout content = findViewById(R.id.fragmentContainer);

        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
        if (fragment == null) {
            fragment = HomeFragment.newInstance();
        }
        replaceFragment(fragment, TAG_FRAGMENT_TWO);
        currentFragmentId = fragment.getId();

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
            fetchData.execute(true);
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
            if (!homeActive) {
                fetchData.execute(false);
                fetchData.afterDownload();
            } else {
                adapter.addAll(new ArrayList<Item>()); // Clear item list if refreshed.
                currentPage = 0;
                fetchData.execute(true);
                fetchData.afterDownload();
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            btnAdd = findViewById(R.id.action_add);
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
                    if (btnAdd != null) {
                        btnAdd.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
                case R.id.nav_shoplist: {
                    Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_THREE);
                    if (fragment == null) {
                        fragment = ShopListsPreviewFragment.newInstance();
                    }
                    replaceFragment(fragment, TAG_FRAGMENT_THREE);
                    if (btnAdd != null) {
                        btnAdd.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
            return true;
        }
    };

    private void replaceFragment(@NonNull Fragment fragment, @NonNull String tag) {
        if (!fragment.equals(currentFragmentId)) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, tag)
                    .commit();
            currentFragmentId = fragment.getId();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //
        getMenuInflater().inflate(R.menu.app_bar_items, menu);
        bindShopsAdapter();
        return true;
    }

    public void bindShopsAdapter() {
        Spinner spinner = findViewById(R.id.spnrShopList);
//        String[] shopsToShow = new String[shops.size() + 1];
//        shopsToShow[0] = getString(R.string.all_shops);
//        for (int i = 0; i < shops.size(); ++i) {
//            shopsToShow[i + 1] = shops.get(i).getName();
//        }

        String[] shopsToShow = new String[shops.size()];
        for (int i = 0; i < shops.size(); ++i) {
            shopsToShow[i] = shops.get(i).getName();
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, shopsToShow);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    selectedShop = null;
//                    MainActivity.this.adapter.returnToOld();
//                } else {
//                selectedShop = shops.get(position - 1);
                if (selectedShop.getId() != shops.get(position).getId()) {
                    selectedShop = shops.get(position);
                    selectedCategory = "";
//                    MainActivity.this.adapter.filter("shopId", String.valueOf(selectedShop.getId()), false);
                    MainActivity.this.adapter.addAll(new ArrayList<Item>()); // Clear item list if refreshed.
                    MainActivity.this.currentPage = 0;
                    MainActivity.this.fetchData.downloadItems(MainActivity.this.getCurrentConfiguration());
                }
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (shops.size() > 0 && selectedShop == null) {
            selectedShop = shops.get(0);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                //
                return true;
            case R.id.action_choose_categories:
                // Show dialog with categories.
                //
                MainActivity.this.getCategories(String.valueOf(selectedShop.getId()));
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

    public String getCurrentConfiguration() {
        String shopId;
        if (selectedShop == null) {
            shopId = "1";
        } else {
            shopId = String.valueOf(selectedShop.getId());
        }
        if (currentPage == 0) {
            currentPage = 1;
        }
        return new APIRequests.ItemsGETRequest(shopId, selectedCategory, String.valueOf(currentPage)).getURL();
    }


    private View.OnClickListener addShopList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle(R.string.new_shoplist);
            alertDialog.setMessage(R.string.how_to_call);

            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            input.setSingleLine(true);
            alertDialog.setView(input);

            alertDialog.setPositiveButton(R.string.okk,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String shopListName = input.getText().toString();
                            if (TextUtils.isEmpty(shopListName)) {
                                Toast.makeText(MainActivity.this, R.string.please_enter_name, Toast.LENGTH_LONG).show();
                            } else {
                                MainActivity.this.postAddShopList(shopListName);
                            }
                        }
                    });

            alertDialog.setNegativeButton(R.string.cancell,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }

    };

    private void postAddShopList(final String name) {
        fetchData.beforeDownload();
        Response.Listener<String> respListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.error_adding_sl)
                            .setNeutralButton(R.string.neutral_ok, null)
                            .create()
                            .show();
                } else {
                    ShopList shopList;
                    try {
                        shopList = ShopList.fromJSONObject(new JSONObject(response));
                        if (shopList.getName().equals(name)) {
                            Toast.makeText(MainActivity.this, getString(R.string.created) + name, Toast.LENGTH_LONG).show();
                            MainActivity.this.fetchData.execute(false);
                            MainActivity.this.fetchData.afterDownload();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MainActivity.this.fetchData.afterDownload();
                }
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                Toast.makeText(MainActivity.this, R.string.error_adding_sl, Toast.LENGTH_LONG).show();
            }
        };

        Map<String, String> namePayload = new HashMap<>();
        namePayload.put("name", name);
        final JSONObject jsonPayload = JSONUtil.formPayload(namePayload);

        String userToken = SharedPrefsUtil.getStringPref(this, Config.KEY_TOKEN);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);

        APIRequests.RequestHandler rh = APIRequests.formPOSTRequest(
                true,
                jsonPayload,
                headers,
                Config.URL_SHOPLIST,
                respListener,
                errListener,
                new WeakReference<>((Context) this)
        );

        rh.launch();
    }

    private void getCategories(String shopId) {
        fetchData.beforeDownload();
        final Response.Listener<String> respListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.error_loading_categories)
                            .setNeutralButton(R.string.neutral_ok, null)
                            .create()
                            .show();
                } else {
                    List<String> categs = new ArrayList<>();
                    try {
                        JSONArray ja = new JSONArray(response);
                        for (int i = 0; i < ja.length(); i++) {
                            categs.add((String) ja.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MainActivity.this.categories = categs;
                    fetchData.afterDownload();

                    final String[] categories = new String[MainActivity.this.categories.size() + 1];
                    categories[0] = getString(R.string.all_categs);
                    for (int i = 1; i < categories.length; i++) {
                        categories[i] = MainActivity.this.categories.get(i - 1);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.select_category);
                    builder.setItems(categories, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int pos) {
                            if (categories[pos].equals(getString(R.string.all_categs))) {
                                selectedCategory = "";
                            } else {
                                selectedCategory = categories[pos];
                            }
                            MainActivity.this.adapter.addAll(new ArrayList<Item>()); // Clear item list if refreshed.
                            MainActivity.this.currentPage = 0;
                            fetchData.execute(true);
                            MainActivity.this.fetchData.afterDownload();
//                            MainActivity.this.fetchData.downloadItems(MainActivity.this.getCurrentConfiguration());
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                Toast.makeText(MainActivity.this, R.string.error_loading_categories, Toast.LENGTH_LONG).show();
            }
        };

        APIRequests.RequestHandler rh = APIRequests.formGETRequest(
                Config.URL_SALES_SHOP + shopId + "/" + Config.URL_CATEGORIES,
                null,
                respListener,
                errListener,
                new WeakReference<>((Activity) MainActivity.this)
        );

        rh.launch();
    }
}


// EOF

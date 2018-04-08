package com.hes.easysales.easysales;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hes.easysales.easysales.activities.MainActivity;
import com.hes.easysales.easysales.activities.ShopListActivity;
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

public class FetchData {

    private ProgressBar pbLoading;

    // To prevent the leak of Context.
    //
    private WeakReference<Activity> activityRef;
    private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutRef;

    public FetchData(Activity a, SwipeRefreshLayout sl) {
        activityRef = new WeakReference<>(a);
        swipeRefreshLayoutRef = new WeakReference<>(sl);
        if (a instanceof MainActivity) {
            pbLoading = a.findViewById(R.id.pbLoading);
        } else if (a instanceof ShopListActivity) {
            pbLoading = a.findViewById(R.id.pbSlLoading);
        }
    }

    public void execute(boolean downloadItems) {
        beforeDownload();
        downloadShops(downloadItems);
//        downloadItems();
        downloadShopLists();
//        afterDownload();
    }

    public void downloadShops(final boolean itemsDownload) {
        Response.Listener<String> respListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activityRef.get());
                    builder.setMessage(R.string.errorLoadingShops)
                            .setNeutralButton(R.string.neutral_ok, null)
                            .create()
                            .show();
                } else {
                    List<Shop> shops = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            shops.add(Shop.fromJSONObject(jo));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (((MainActivity) activityRef.get()).shops.size() == 0) {
                        ((MainActivity) activityRef.get()).shops.clear();
                        ((MainActivity) activityRef.get()).shops.addAll(shops);
                        ((MainActivity) activityRef.get()).bindShopsAdapter();
                    }
                    if (itemsDownload) {
                        FetchData.this.downloadItems(((MainActivity) activityRef.get()).getCurrentConfiguration());
                    }
                }
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                Toast.makeText(activityRef.get(), R.string.errorLoadingShops, Toast.LENGTH_LONG).show();
            }
        };

        APIRequests.RequestHandler rh = APIRequests.formGETRequest(
                Config.URL_SALES_SHOP,
                null,
                respListener,
                errListener,
                new WeakReference<>(activityRef.get())
        );
        rh.launch();
    }

    public void downloadItems(String itemsURL) {
        Log.d("asadfasdffdsa", "onResponse: " + ((MainActivity) activityRef.get()).getCurrentConfiguration());
        beforeDownload();
        Response.Listener<String> respListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activityRef.get());
                    builder.setMessage(R.string.errorLoadingItems)
                            .setNeutralButton(R.string.neutral_ok, null)
                            .create()
                            .show();
                } else {
                    List<Item> items = new ArrayList<>();
                    try {
                        ((MainActivity) activityRef.get()).totalItemsCount = new JSONObject(response).getInt("count");
                        JSONArray jsonArray = new JSONObject(response).getJSONArray("rows");
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            items.add(Item.fromJSONObject(jo));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((MainActivity) activityRef.get()).adapter.appendAll(items);
                    FetchData.this.afterDownload();
//                    ((MainActivity) activityRef.get()).adapter.subsItemsWithTemp();
                }
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                Toast.makeText(activityRef.get(), R.string.errorLoadingItems, Toast.LENGTH_LONG).show();
            }
        };


        APIRequests.RequestHandler rh = APIRequests.formGETRequest(
                itemsURL,
                null,
                respListener,
                errListener,
                new WeakReference<>(activityRef.get())
        );
        rh.launch();
    }

    public void downloadShopLists() {
        Response.Listener<String> respListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activityRef.get());
                    builder.setMessage(R.string.errorLoadingShopLists)
                            .setNeutralButton(R.string.neutral_ok, null)
                            .create()
                            .show();
                } else {
                    List<ShopList> shopLists = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            shopLists.add(ShopList.fromJSONObject(jo));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((MainActivity) activityRef.get()).shopListsPreviewAdapter.addAll(shopLists);
//                    FetchData.this.afterDownload();
                }
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                Toast.makeText(activityRef.get(), R.string.errorLoadingShopLists, Toast.LENGTH_LONG).show();
            }
        };

        String userToken = SharedPrefsUtil.getStringPref(activityRef.get(), Config.KEY_TOKEN);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);

        if (((MainActivity) activityRef.get()).isLoggedIn()) {
            APIRequests.RequestHandler rh = APIRequests.formGETRequest(
                    Config.URL_SHOPLISTS,
                    headers,
                    respListener,
                    errListener,
                    new WeakReference<>(activityRef.get())
            );
            rh.launch();
        }
    }

    public void downloadCurrentShopList(final long id) {
        beforeDownload();
        Response.Listener<String> respListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activityRef.get());
                    builder.setMessage(R.string.error_loading_this_list)
                            .setNeutralButton(R.string.neutral_ok, null)
                            .create()
                            .show();
                } else {
                    ShopList shopList = null;
                    try {
                        shopList = ShopList.fromJSONObject(new JSONObject(response));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((ShopListActivity) activityRef.get()).selectedShopList = shopList;
                    ((ShopListActivity) activityRef.get()).adapter.clearTmpItems();
                    ((ShopListActivity) activityRef.get()).adapter.addAllTmpItems(shopList.getItems());
                    ((ShopListActivity) activityRef.get()).adapter.addAllTmpItems(shopList.getCustomItems());
                    ((ShopListActivity) activityRef.get()).adapter.subsItemsWithTemp();
                    ((ShopListActivity) activityRef.get()).adapter.notifyDataSetChanged();
                    FetchData.this.afterDownload();
                }
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                Toast.makeText(activityRef.get(), R.string.error_loading_this_list, Toast.LENGTH_LONG).show();
            }
        };

        String userToken = SharedPrefsUtil.getStringPref(activityRef.get(), Config.KEY_TOKEN);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);

        APIRequests.RequestHandler rh = APIRequests.formGETRequest(
                Config.URL_SHOPLIST + id,
                headers,
                respListener,
                errListener,
                new WeakReference<>(activityRef.get())
        );

        rh.launch();
    }

    public void beforeDownload() {
        if (!pbLoading.isShown()) {
            pbLoading.setVisibility(View.VISIBLE);
            if (activityRef.get() instanceof MainActivity) {
                ((MainActivity) activityRef.get()).adapter.clearTmpItems();
            } else if (activityRef.get() instanceof ShopListActivity) {

            }
        }
    }

    public void afterDownload() {
        // Stop animation of refreshing.
        //
        if (pbLoading.isShown()) {
            swipeRefreshLayoutRef.get().setRefreshing(false);
            pbLoading.setVisibility(View.GONE);
        }
        if (activityRef.get() instanceof MainActivity) {
            ((MainActivity) activityRef.get()).filterSelectedShops();
        } else if (activityRef.get() instanceof ShopListActivity) {
            swipeRefreshLayoutRef.get().setRefreshing(false);
            pbLoading.setVisibility(View.GONE);
        }
    }
}


// EOF

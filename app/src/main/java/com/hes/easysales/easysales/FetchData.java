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
import com.hes.easysales.easysales.utilities.SharedPrefsUtil;
import com.hes.easysales.easysales.utilities.ShopsUtil;

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
        pbLoading = a.findViewById(R.id.pbLoading);
    }

    public void execute() {
        beforeDownload();
        downloadItems();
        downloadShopLists();
//        afterDownload();
    }

    private void downloadItems() {
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
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            items.add(Item.fromJSONObject(jo));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((MainActivity) activityRef.get()).adapter.addAllTmpItems(items);
                    ((MainActivity) activityRef.get()).adapter.subsItemsWithTemp();
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
                ShopsUtil.getShopUrlWithItemsById(Config.DIXY_SHOP_ID),
                null,
                respListener,
                errListener,
                new WeakReference<>(activityRef.get().getApplicationContext())
        );
        rh.launch();

        APIRequests.RequestHandler rh1 = APIRequests.formGETRequest(
                ShopsUtil.getShopUrlWithItemsById(Config.PEREKRESTOK_SHOP_ID),
                null,
                respListener,
                errListener,
                new WeakReference<>(activityRef.get().getApplicationContext())
        );
        rh1.launch();
    }

    private void downloadShopLists() {
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

        APIRequests.RequestHandler rh = APIRequests.formGETRequest(
                Config.URL_SHOPLISTS,
                headers,
                respListener,
                errListener,
                new WeakReference<>(activityRef.get().getApplicationContext())
        );

        rh.launch();
    }

    private void beforeDownload() {
        pbLoading.setVisibility(View.VISIBLE);
        ((MainActivity) activityRef.get()).adapter.clearTmpItems();
    }

    public void afterDownload() {
        // Stop animation of refreshing.
        //
        swipeRefreshLayoutRef.get().setRefreshing(false);
        pbLoading.setVisibility(View.GONE);
    }
}


// EOF

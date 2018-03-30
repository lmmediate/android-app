package com.hes.easysales.easysales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;

import com.hes.easysales.easysales.activities.MainActivity;
import com.hes.easysales.easysales.adapters.ItemAdapter;
import com.hes.easysales.easysales.utilities.ShopsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinopsys on 2/18/18.
 */

public class FetchData extends AsyncTask<Void, Void, List<Item>> {

    private StringBuilder data = new StringBuilder();

    private HttpURLConnection httpURLConnection;
    private ProgressDialog pdLoading;

    // To prevent the leak of Context.
    //
    private WeakReference<Activity> activityRef;
    private WeakReference<SwipeRefreshLayout> swipeRefreshLayoutRef;

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds.
    //
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 15000;

    public FetchData(Activity a, SwipeRefreshLayout sl) {
        activityRef = new WeakReference<>(a);
        swipeRefreshLayoutRef = new WeakReference<SwipeRefreshLayout>(sl);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        String loading = activityRef.get()
                .getApplicationContext().getString(R.string.loadingTitle);

        pdLoading = new ProgressDialog(activityRef.get());
        pdLoading.setMessage("\t" + loading);
        pdLoading.setCancelable(false);
        pdLoading.show();
    }

    @Override
    protected List<Item> doInBackground(Void... voids) {
        try {
            URL url = new URL(ShopsUtil.getShopUrlWithItemsById(Config.DIXY_SHOP_ID));
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.setRequestMethod("GET");

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = br.readLine()) != null) {
                    data.append(line);
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }

        List<Item> items = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(this.data.toString());
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jo = null;
                jo = jsonArray.getJSONObject(i);
                items.add(Item.fromJSONObject(jo));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        super.onPostExecute(items);
        if (pdLoading.isShowing()) {
            pdLoading.dismiss();
        }

        RecyclerView rv = (activityRef.get()).findViewById(R.id.itemList);
        if (rv != null) {
            ItemAdapter adapter = (ItemAdapter) rv.getAdapter();
            adapter.addAll(items);
        }

        // Stop animation of refreshing.
        //
        swipeRefreshLayoutRef.get().setRefreshing(false);
    }
}


// EOF

package com.hes.easysales.easysales.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hes.easysales.easysales.APIRequests;
import com.hes.easysales.easysales.Config;
import com.hes.easysales.easysales.FetchData;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.ShopList;
import com.hes.easysales.easysales.adapters.ItemAdapter;
import com.hes.easysales.easysales.utilities.SharedPrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hes.easysales.easysales.Config.KEY_CURRENT_SHOPLIST;

public class ShopListActivity extends AppCompatActivity {

    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvShopList;
    SwipeRefreshLayout swipeRefreshLayout;
    public ItemAdapter adapter;
    public ShopList selectedShopList;
    public FetchData fetchData;
    private View btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rvShopList = findViewById(R.id.rvShopList);
        swipeRefreshLayout = findViewById(R.id.swipeSlContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((ProgressBar) findViewById(R.id.pbSlLoading)).setProgress(0, true);
        } else {
            ((ProgressBar) findViewById(R.id.pbSlLoading)).setProgress(0);
        }
        fetchData = new FetchData(this, swipeRefreshLayout);

        layoutManager = new LinearLayoutManager(this);
        rvShopList.setLayoutManager(layoutManager);

        adapter = new ItemAdapter(new ArrayList<Item>(), this);
        rvShopList.setAdapter(adapter);

        ShopList sl = getIntent().getExtras().getParcelable(KEY_CURRENT_SHOPLIST);
        selectedShopList = sl;
        fetchData.downloadCurrentShopList(selectedShopList.getId());
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        btnAdd = findViewById(R.id.action_item_add);
        btnAdd.setOnClickListener(addCustomItem);
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            ShopListActivity.this.adapter.addAll(new ArrayList<Item>());
            layoutManager = new LinearLayoutManager(ShopListActivity.this);
            rvShopList.setLayoutManager(layoutManager);
            fetchData.downloadCurrentShopList(selectedShopList.getId());
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.collapseActionView:
                return true;

            default:
                onBackPressed();
                return true;
        }
    }

    private View.OnClickListener addCustomItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShopListActivity.this);
            alertDialog.setTitle(R.string.add_custom_item);
            alertDialog.setMessage(R.string.title_add_custom_item);

            final EditText input = new EditText(ShopListActivity.this);
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
                                Toast.makeText(ShopListActivity.this, R.string.please_enter_name, Toast.LENGTH_LONG).show();
                            } else {
                                ShopListActivity.this.postItemAdd(shopListName);
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


    private void postItemAdd(final String name) {
        fetchData.beforeDownload();
        Response.Listener<String> respListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopListActivity.this);
                    builder.setMessage(R.string.error_adding_sl)
                            .setNeutralButton(R.string.neutral_ok, null)
                            .create()
                            .show();
                } else {
                    Item item;
                    try {
                        item = Item.fromJSONObject(new JSONObject(response));
                        if (item.getName().equals(name)) {
                            Toast.makeText(ShopListActivity.this, getString(R.string.item_added) + " " + name, Toast.LENGTH_LONG).show();
                            ShopListActivity.this.adapter.addAll(new ArrayList<Item>());
                            layoutManager = new LinearLayoutManager(ShopListActivity.this);
                            rvShopList.setLayoutManager(layoutManager);
                            fetchData.downloadCurrentShopList(selectedShopList.getId());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ShopListActivity.this.fetchData.afterDownload();
                }
            }
        };

        Response.ErrorListener errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Config.TAG_VOLLEY_ERROR, error.toString());
                Toast.makeText(ShopListActivity.this, R.string.error_adding_sl, Toast.LENGTH_LONG).show();
            }
        };

        String userToken = SharedPrefsUtil.getStringPref(this, Config.KEY_TOKEN);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userToken);

        APIRequests.RequestHandler rh = APIRequests.formPOSTRequest(
                false,
                null,
                headers,
                new APIRequests.ShopListPOSTRequest(String.valueOf(selectedShopList.getId()), name).getAddItemURL(),
                respListener,
                errListener,
                new WeakReference<>((Context) ShopListActivity.this)
        );

        rh.launch();
    }
}


// EOF

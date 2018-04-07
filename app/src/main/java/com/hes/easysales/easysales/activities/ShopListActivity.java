package com.hes.easysales.easysales.activities;

import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hes.easysales.easysales.FetchData;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.ShopList;
import com.hes.easysales.easysales.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.hes.easysales.easysales.Config.KEY_CURRENT_SHOPLIST;

public class ShopListActivity extends AppCompatActivity {

    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvShopList;
    SwipeRefreshLayout swipeRefreshLayout;
    public ItemAdapter adapter;
    public ShopList selectedShopList;
    public FetchData fetchData;

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

//        adapter.addAllTmpItems(sl.getItems());
//        adapter.addAllTmpItems(sl.getCustomItems());
//        adapter.subsItemsWithTemp();
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
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.collapseActionView:
                return true;

            default:
                onBackPressed();
                return true;
        }
    }
}


// EOF

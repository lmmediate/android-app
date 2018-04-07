package com.hes.easysales.easysales.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

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
    public ItemAdapter adapter;
    public ShopList selectedShopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rvShopList = findViewById(R.id.rvShopList);

        layoutManager = new LinearLayoutManager(this);
        rvShopList.setLayoutManager(layoutManager);

        adapter = new ItemAdapter(new ArrayList<Item>(), this);
        rvShopList.setAdapter(adapter);

        ShopList sl = getIntent().getExtras().getParcelable(KEY_CURRENT_SHOPLIST);
        selectedShopList = sl;

        adapter.addAllTmpItems(sl.getItems());
        adapter.addAllTmpItems(sl.getCustomItems());
        adapter.subsItemsWithTemp();
    }

    @Override
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

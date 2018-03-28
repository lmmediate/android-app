package com.hes.easysales.easysales.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinopsys on 2/25/18.
 */

public class ShopListFragment extends Fragment {

    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvShopList;
    List<Item> shopListItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shoplist, container, false);
        rvShopList = v.findViewById(R.id.rvShopList);
        layoutManager = new LinearLayoutManager(getActivity());
        rvShopList.setLayoutManager(layoutManager);

        setData();
        return v;
    }

    private void setData() {
        for (int i = 0; i < 20; ++i) {
            Item item = new Item("Name " + String.valueOf(i),
                    "Name " + String.valueOf(i),
                    "https://dixy.ru/upload/iblock/814/2000148579.jpg",
                    i + 100,
                    i + 50,
                    i,
                    "Disc " + String.valueOf(i),
                    "Datein " + String.valueOf(i),
                    "Dateout " + String.valueOf(i),
                    "cond " + String.valueOf(i));

            item.setExpandable(true);
            shopListItems.add(item);
        }

        ItemAdapter adapter = (ItemAdapter) rvShopList.getAdapter();
        if (adapter == null) {
            adapter = new ItemAdapter(shopListItems, getActivity());
            rvShopList.setAdapter(adapter);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.shopListItems = new ArrayList<>();
    }
}


// EOF

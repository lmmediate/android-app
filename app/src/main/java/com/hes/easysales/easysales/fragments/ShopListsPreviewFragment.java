package com.hes.easysales.easysales.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.ShopList;
import com.hes.easysales.easysales.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinopsys on 3/30/18.
 */

public class ShopListsPreviewFragment extends Fragment {

    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvShopLists;
    List<ShopList> shopLists;

    public static ShopListsPreviewFragment newInstance() {
        ShopListsPreviewFragment spf = new ShopListsPreviewFragment();
        Bundle args = new Bundle();
        spf.setArguments(args);
        return spf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_shoplists, container, false);
        rvShopLists = v.findViewById(R.id.rvAllShopLists);
//        setData();
        return v;
    }

//    private void setData() {
//        for (int i = 0; i < 40; i++) {
//            List<Item> itemList = new ArrayList<>();
//            for (int j = 0; j < 6; j++) {
//                Item iii = new Item();
//                iii.setName("asdf" + j);
//                itemList.add(iii);
//            }
//            shopLists.add(new ShopList("name" + i, new ArrayList<Item>(), itemList));
//        }
//        ((MainActivity) getActivity()).shopListsPreviewAdapter.addAll(shopLists);
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.shopLists = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rvShopLists.setAdapter(((MainActivity) getActivity()).shopListsPreviewAdapter);
        rvShopLists.setLayoutManager(layoutManager);
        layoutManager.onRestoreInstanceState(((MainActivity) getActivity()).shopListsPreviewFragmentState);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).shopListsPreviewFragmentState = layoutManager.onSaveInstanceState();
    }
}


// EOF

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

public class HomeFragment extends Fragment {

    List<Item> items;
    RecyclerView rvItemList;
    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        rvItemList = v.findViewById(R.id.itemList);
        layoutManager = rvItemList.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(getActivity());
            rvItemList.setLayoutManager(layoutManager);
        }

        setData();
        return v;
    }

    private void setData() {
        ItemAdapter adapter = (ItemAdapter) rvItemList.getAdapter();
        if (adapter == null) {
            adapter = new ItemAdapter(items, getActivity());
            rvItemList.setAdapter(adapter);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.items = new ArrayList<>();
    }

}


// EOF

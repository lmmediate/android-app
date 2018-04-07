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
import com.hes.easysales.easysales.activities.MainActivity;

/**
 * Created by sinopsys on 3/30/18.
 */

public class ShopListsPreviewFragment extends Fragment {

    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvShopLists;

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
        return v;
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
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).fetchData.downloadShopLists();
        layoutManager.onRestoreInstanceState(((MainActivity) getActivity()).shopListsPreviewFragmentState);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).shopListsPreviewFragmentState = layoutManager.onSaveInstanceState();
    }
}


// EOF

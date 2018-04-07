package com.hes.easysales.easysales.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hes.easysales.easysales.APIRequests;
import com.hes.easysales.easysales.EndlessRCVScrollListener;
import com.hes.easysales.easysales.Item;
import com.hes.easysales.easysales.R;
import com.hes.easysales.easysales.activities.MainActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by sinopsys on 2/25/18.
 */

public class HomeFragment extends Fragment {

    private RecyclerView rvItemList;
    private RecyclerView.LayoutManager layoutManager;

    public static HomeFragment newInstance() {
        HomeFragment hf = new HomeFragment();
        Bundle args = new Bundle();
        hf.setArguments(args);
        return hf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        rvItemList = v.findViewById(R.id.itemList);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutManager = new LinearLayoutManager(getActivity());
        rvItemList.setAdapter(((MainActivity) getActivity()).adapter);
        rvItemList.setLayoutManager(layoutManager);
        layoutManager.onRestoreInstanceState(((MainActivity) getActivity()).itemsFragmentState);
        ((MainActivity) getActivity()).homeActive = true;

        EndlessRCVScrollListener ercvl = new EndlessRCVScrollListener((LinearLayoutManager) (rvItemList).getLayoutManager(), new WeakReference<Activity>((MainActivity) getActivity())) {
            @Override
            public void onLoadMore(int currentPage) {
                ((MainActivity) getActivity()).currentPage = currentPage;
                if (currentPage > 1) {
                    ((MainActivity) getActivity()).fetchData.downloadItems(((MainActivity) getActivity()).getCurrentConfiguration());
                }
            }
        };
        rvItemList.addOnScrollListener(ercvl);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).itemsFragmentState = layoutManager.onSaveInstanceState();
        ((MainActivity) getActivity()).homeActive = false;
    }
}


// EOF
